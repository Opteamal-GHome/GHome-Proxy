import java.util.Arrays;


public class Utilitaires {
	
	public static byte[] intToByteArray(int data)
	{
		byte[] byteArray = new byte[4];
		int i = 0;
		int shift = 24;
		for(i = 0; i < 4; i++)
		{
			byteArray[i] = (byte)(data >> shift & 0xff); 
			shift -= 8;
		}
		return byteArray;
	}
	
	public static byte[] concat(byte[] first, byte[] second) {
		byte[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
		}
	
	public static byte[] toByteArray(long l) {
	     return new byte[] { 
	        (byte)((l >> 56) & 0xff),
	         (byte)((l >> 48) & 0xff),
	         (byte)((l >> 40) & 0xff),
	         (byte)((l >> 32) & 0xff),
	         (byte)((l >> 24) & 0xff),
	         (byte)((l >> 16) & 0xff),
	         (byte)((l >> 8) & 0xff),
	         (byte)((l >> 0) & 0xff),
	     };
	 }

}
