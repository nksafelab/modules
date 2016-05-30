package jinke.readings.util;
import java.io.*;

public class StrToHex
{ 
 static Integer outIng;
 static String unicodeStr;
 static boolean isSend = true;

    public static void main(String[] args)
 {     try{
//                InputStreamReader reader = new InputStreamReader(System.in);
//    BufferedReader input = new BufferedReader(reader);
//    System.out.println("Enter your number:(input end,the app exit) ");
//    String name = input.readLine();
//       
//                if(name !="end")
//    {    
                	System.out.println(encode("中文"));
                	System.out.println(decode(encode("中文")));
       
   // }
         }catch(Exception ex){
    System.out.println("Exception :"+ex);
   }
     }
 
///////start--->666666666666666中文转换成uncode,又转成中文
//http://community.csdn.net/Expert/topic/5533/5533122.xml?temp=.3077356
//System.out.println(encode("中文"));
//System.out.println(decode(encode("中文")));
/*
 * 16进制数字字符集
 */
private static String hexString="0123456789ABCDEF";
/*
 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
 */
public static String encode(String str) throws UnsupportedEncodingException
{
 //根据"gb2312"编码获取字节数组
 byte[] bytes=str.getBytes("gb2312");
 StringBuilder sb=new StringBuilder(bytes.length*2);
 //将字节数组中每个字节拆解成2位16进制整数
  for(int i=0;i<bytes.length;i++)
  {
  sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
  sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
  }
  return sb.toString();
}
/*
 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
 */
public static String decode(String bytes) throws UnsupportedEncodingException
{
 ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2);
 //将每2位16进制整数组装成一个字节
 for(int i=0;i<bytes.length();i+=2)
 baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
 return new String(baos.toByteArray(),"gb2312");
}
///////End--->666666666666666中文转换成uncode,又转成中文

}