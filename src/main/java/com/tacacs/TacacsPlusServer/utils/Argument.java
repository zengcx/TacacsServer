package com.tacacs.TacacsPlusServer.utils;

/**
 * 授权数据包
 */
public class Argument {

    public final String attribute;
    public final String value;
    final boolean isOptional;

    public Argument(String attribute, String value, boolean isOptional){
        this.attribute = attribute;
        this.value = value;
        this.isOptional = isOptional;
    }
    /** 对从服务端读取的数据包进行转换 **/
    public Argument(String arg){
        String[] args = arg.split("[=*]", 2);
        this.attribute=args[0];
        if (args.length==2) { this.value=args[1]; } else { this.value = null; }
        isOptional = (arg.length() > attribute.length()) && // there is another character after the attribute name
                (arg.charAt(attribute.length()) == '*');
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public String toString()
    {
        return attribute+(isOptional?"*":"=")+(value==null?"":value);
    }
}
