package org.joinsip.common;

import java.io.PrintStream;

public class JsSystem
{
  public static JsPrintStream err;
  public static JsPrintStream out = new JsPrintStream()
  {
    public void print(String paramString)
    {
      System.out.print(paramString);
    }

    public void println(String paramString)
    {
      System.out.println(paramString);
    }
  };

  static
  {
    err = new JsPrintStream()
    {
      public void print(String paramString)
      {
        System.err.print(paramString);
      }

      public void println(String paramString)
      {
        System.err.println(paramString);
      }
    };
  }

  public static final long currentTimeMillis()
  {
    return System.nanoTime() / 1000000L;
  }

  public static final void exit(int paramInt)
  {
    System.exit(paramInt);
  }

  public static final void setErr(JsPrintStream paramJsPrintStream)
  {
    err = paramJsPrintStream;
  }

  public static final void setOut(JsPrintStream paramJsPrintStream)
  {
    out = paramJsPrintStream;
  }
}

/* Location:           C:\Users\Administrator\Documents\Tencent Files\1946940079\FileRecv\classes-dex2jar\
 * Qualified Name:     org.joinsip.common.JsSystem
 * JD-Core Version:    0.6.0
 */