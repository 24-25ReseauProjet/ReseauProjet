import java.util.Scanner;

public class UserInputHandler {
    private Scanner scanner;
    private UI ui;

    public  UserInputHandler(UI ui){
        this.ui = ui;
        this.scanner = new Scanner(System.in);
    }

    //处理用户输入的主方法
    public String getUserInput() {
        ui.appendToOutput("Please enter a letter: ");
        return ui.waitForUserInput(); // 等待图形界面用户输入
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
