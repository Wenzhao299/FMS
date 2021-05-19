package VideoConference;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Database {
    //用于存储用户名和密码
    public Map<String,String> customer=new HashMap<String,String>();
    private ArrayList<String> name=new ArrayList<>();
    private ArrayList<String> password=new ArrayList<>();


    //MD5 Hash
    public  String getMd5(String source) throws NoSuchAlgorithmException, NoSuchAlgorithmException {
        //1.获取MessageDigest对象
        MessageDigest digest = MessageDigest.getInstance("md5");

        //2.执行加密操作
        byte[] bytes = source.getBytes();

        //在MD5算法这，得到的目标字节数组的特点：长度固定为16
        byte[] targetBytes = digest.digest(bytes);

        //3.声明字符数组
        char[] characters = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        //4.遍历targetBytes
        StringBuilder builder = new StringBuilder();
        for (byte b : targetBytes) {
            //5.取出b的高四位的值
            //先把高四位通过右移操作拽到低四位
            int high = (b >> 4) & 15;

            //6.取出b的低四位的值
            int low = b & 15;

            //7.以high为下标从characters中取出对应的十六进制字符
            char highChar = characters[high];

            //8.以low为下标从characters中取出对应的十六进制字符
            char lowChar = characters[low];

            builder.append(highChar).append(lowChar);
        }

        return builder.toString();
    }

    //判断是否是会议的用户
    public boolean IsCustomer(String str){
        if(customer.get(str)!=null)
            return true;
        else return false;
    };

    //添加用户
    public boolean AddCustomer(String str1,String str2){
        if(str1!=null && str2!=null){
                customer.put(str1,str2);
                return true;
        }
        else return false;

    }

    //获取用户的数目
    public int GetNumber(){
        return customer.size();
    }

    //用户ID是否在库里
    public boolean IsIDinarray(String str){
        boolean result=false;
        for(int i=0;i<name.size();i++){

            if(name.get(i).equals(str))
                result=true;
        }
        return result;
    }


    //获取用户的ID
    public ArrayList<String> GetName(){
        int i=0;
        int size=customer.size();
        Set<String> keySet = customer.keySet();
        //遍历存放所有key的Set集合
        Iterator<String> it =keySet.iterator();
        while(i<size && it.hasNext()){
            String key=it.next();
            if(!IsIDinarray(key)){
                name.add(key);
            }
        }
        return name;
    }

    //获取用户的password 的hash
    public ArrayList<String> GetPassword(){
        int i=0;
        int size=customer.size();
        Set<String> keySet = customer.keySet();
        //遍历存放所有key的Set集合
        Iterator<String> it =keySet.iterator();
        while(i<size && it.hasNext()){
            String key=it.next();
            if(!IsIDinarray(key)){
                password.add(customer.get(key));
            }
        }
        return password;
    }



    public Map<String, String> setCustomer() throws NoSuchAlgorithmException {
        for(int i=0;i<10;i++){
            String str1="customer"+i;
            String str2=getMd5(str1);
            customer.put(str1,str2);
        }
        return customer;
    }

    //将Arraylist变为一个String
    public String GetArraytoString(ArrayList<String> array){
        String str="";
        for(int i=0;i<array.size()-1;i++){
            str=str+array.get(i)+",";
        }
        str=str+array.get(array.size());
        return str;
    }

    //将String转化为ArrayList
    public ArrayList<String> Trans_str_to_array(String str){
        ArrayList<String> array = new ArrayList(Arrays.asList(str.split(",")));
        return array;
    }

}


