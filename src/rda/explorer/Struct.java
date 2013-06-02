package rda.explorer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;

/**
 * "C"-Struct.
 *
 * {@code long} = DWORD = 4 byte,
 * {@code int} = WORD = 2 byte,
 * {@code short} = BYTE = 1 byte
 *
 * <p>
 * http://blog.nigjo.de/netbeans/2010/08/cstruct/
 * @author nigjo
 */
public abstract class Struct
{
  private static final long DWORD_MASK = 0xFFFFFFFFl;
  private static final int WORD_MASK = 0xFFFF;
  private static final short BYTE_MASK = 0xFF;

  private int getSize()
  {
    Size size = getClass().getAnnotation(Size.class);
    if(size == null)
      throw new IllegalStateException(
          "missing @Size for " + getClass().getName());
    return size.value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  public @interface Size
  {
    int value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  protected @interface NoStructElement
  {
  }

  public void read(InputStream s) throws IOException
  {
    ByteBuffer buffer = ByteBuffer.allocate(getSize());
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    s.read(buffer.array());
    read(buffer);
  }

  public void read(ByteBuffer parent, int offset)
  {
    byte[] cache = new byte[getSize()];
    parent.position(offset);
    parent.get(cache);
    ByteBuffer buffer = ByteBuffer.wrap(cache);
    buffer.order(parent.order());
    read(buffer);
  }

  public void read(ByteBuffer buffer)
  {
    if(buffer.capacity() != getSize())
    {
      throw new IllegalArgumentException(
          MessageFormat.format("different buffer size {0}!={1}",
          buffer.capacity(), getSize()));
    }
    try
    {
      Field[] fields = getClass().getDeclaredFields();
      for(Field field : fields)
      {
        if((field.getModifiers() & Modifier.STATIC) != 0)
          continue;
        if(field.getAnnotation(NoStructElement.class) != null)
          continue;
        Class<?> type = field.getType();
        if(type.equals(long.class))
        {
          long value = buffer.getInt() & DWORD_MASK;
          field.setLong(this, value);
        }
        else if(type.equals(int.class))
        {
          int value = buffer.getShort() & WORD_MASK;
          field.setInt(this, value);
        }
        else if(type.equals(short.class))
        {
          short value = (short)(buffer.get() & BYTE_MASK);
          field.setShort(this, value);
        }
        else if(type.equals(char.class))
        {
          char value = (char)(buffer.get() & WORD_MASK);
          field.setChar(this, value);
        }
        else
        {
          Size size = field.getAnnotation(Size.class);
          if(size == null)
          {
            throw new IllegalStateException(
                "missing @Size for " + field.getName());
          }
          if(type.equals(byte[].class))
          {
            byte[] reserved = new byte[size.value()];
            buffer.get(reserved);
            field.set(this, reserved);
          }
          else if(type.equals(Struct.class))
          {
            Struct struct = (Struct)field.get(this);
            if(struct == null)
            {
              struct = type.asSubclass(Struct.class).newInstance();
              field.set(this, struct);
            }
            struct.read(buffer, buffer.position());
          }
          else
          {
            throw new IllegalStateException(
                "unknown type " + type.getName());
          }
        }
      }
      if(buffer.hasRemaining())
        throw new IllegalArgumentException(
            "size missmatch " + buffer.position() + "!=" + buffer.limit());
    }
    catch(InstantiationException iae)
    {
      throw new IllegalStateException(iae);
    }
    catch(IllegalAccessException iae)
    {
      throw new IllegalStateException(iae);
    }
  }
}
