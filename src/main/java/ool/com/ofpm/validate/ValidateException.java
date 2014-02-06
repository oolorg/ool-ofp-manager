package ool.com.ofpm.validate;

public class ValidateException extends Exception{

    private static final long serialVersionUID = 1L;

    public ValidateException(){
	super();
    }
	
    public ValidateException(String mes){
	super(mes);
    }

}
