public class UserInputHandler {
    private UI ui;

    public UserInputHandler(UI ui) {
        this.ui = ui;
    }

    // 删除默认 getUserInput() 方法以防止误调用
    public String getUserInput(String prompt) {
        ui.appendToOutput(prompt);
        return ui.waitForUserInput(); // 从 UI 获取用户输入
    }

    public void close() {
        // 无需在图形界面模式下进行资源释放
    }
}
