package jinke.readings.util;
import java.io.UnsupportedEncodingException;


public class Base64Encode {
	private static final byte[] ch64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-/".getBytes();
	private static final char endFlag = 1;
	private static final char EndFlag2 = '@';
	private static byte[] encode(byte[] src,int srclen){
		
		int dstlen,i,j;
		 byte[] buf;
		 byte[] dst; 

		buf = src;
		dstlen = (srclen+2)*4/3;

		dst = new byte[dstlen+1];

		for(i=0,j=0; i<srclen; i+=3,j+=4){
			dst[j] = ch64[(buf[i]&0xFC)>>2];
			if(i+1>=srclen){
				dst[j+1] = ch64[(buf[i]&0x03)<<4];
				dst[j+2] = EndFlag2;
				dst[j+3] = EndFlag2;
				break;
			}
			dst[j+1] = ch64[((buf[i]&0x03)<<4) + ((buf[i+1]&0xF0)>>4)];
			if(i+2>=srclen){
				dst[j+2] = ch64[(buf[i+1]&0x0F)<<2];
				dst[j+3] = EndFlag2;
				break;
			}
			dst[j+2] = ch64[((buf[i+1]&0x0F)<<2) + ((buf[i+2]&0xC0)>>6)];
			dst[j+3] = ch64[buf[i+2]&0x3F]; 
		}
		return dst;
	}
	
	public static String encodeStr(String str){
		
		byte[] s = null;
		try {
			s = str.getBytes("gb2312");
		} catch (Exception e) {
			// TODO: handle exception
		}
		s = encode(s, s.length);
		String r = new String(s);
		return (r.trim()+"%20");
	}
	
	public static void main(String[] args) {
//		byte[] s = null;
//		try {
//			s = "鲁迅".getBytes("gb2312");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		for(int i = 0;i<ch64.length;i++){
//			System.out.print(ch64[i]+" ");
//		}
//		System.out.println();
//		System.out.println(System.getProperty("file.encoding"));
//	System.out.println("源码:"+new String(s)+"--");
//	s = encode(s, s.length);
//	System.out.println("密码:"+new String(s)+"--");
		String r =encodeStr("中国");
		System.out.println(r+"--");
}
}
