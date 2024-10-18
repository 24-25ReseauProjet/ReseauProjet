import java.util.Scanner;

public class UserInputHandler {
    private Scanner scanner;

    public  UserInputHandler(){
        this.scanner = new Scanner(System.in);
    }

    //处理用户输入的主方法
    public String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim(); // 获取用户输入并去除前后空格
    }

    // 默认使用 "Please enter a letter : " 作为提示
    public String getUserInput() {
        return getUserInput("Please enter a letter : ");
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