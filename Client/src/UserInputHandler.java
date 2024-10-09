import java.util.Scanner;

public class UserInputHandler {
    private Scanner scanner;

    public  UserInputHandler(){
        this.scanner = new Scanner(System.in);
    }

    //处理用户输入的主方法
    public String getUserInput(){
        System.out.println("Please enter a letter : ");
        return scanner.nextLine().trim();//获取用户输入并去除先后空格
    }

    public void close(){
        if(scanner!=null){
            scanner.close();
        }
    }

    public boolean isValideEnter(String input){
        return input.length()==1&&Character.isLetter(input.charAt(0));
    }


}
