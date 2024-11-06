package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        // TaskDataAccessのfindAllメソッドからデータを取得
        List<Task> tasks = taskDataAccess.findAll();

        /*
         * タスクを表示する
         * status の値に応じて表示を変える
         * 0→未着手、1→着手中、2→完了
         * タスクを担当するユーザーの名前が表示できるようにする
         */
        tasks.forEach(task -> {
            String status = "未着手";
            if (task.getStatus() == 1) {
                status = "着手中";
            } else if (task.getStatus() == 2) {
                status = "完了";
            }

            String userName = "あなたが担当しています";
            if (task.getRepUser().getCode() != loginUser.getCode()) {
                User taskUser = userDataAccess.findByCode(task.getRepUser().getCode());
                userName = taskUser.getName() + "が担当しています";
            }

            System.out.println(task.getCode() + ". タスク名：" + task.getName() + ", 担当者名：" + userName +
                    ", ステータス：" + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        // repUserCodeでuserDataAccess#findByCodeからユーザーデータを取得
        User user = userDataAccess.findByCode(repUserCode);
        // ユーザーデータがnullならAppExceptionをスローする
        if (user == null) throw new AppException("存在するユーザーコードを入力してください");

        // Taskデータを作成
        Task task = new Task(code, name, 0, user);
        // TaskDataAccess#saveを呼び、データを保存する
        taskDataAccess.save(task);

        // Logデータを作成
        Log log = new Log(task.getCode(), loginUser.getCode(), 0, LocalDate.now());
        // LogDataAccess#saveを呼び、データを保存する
        logDataAccess.save(log);

        System.out.println(name + "の登録が完了しました。");
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int updateStatus,
                            User loginUser) throws AppException {
        // codeからタスクを取得
        Task task = taskDataAccess.findByCode(code);

        // 入力されたタスクコードが tasks.csvに存在しない場合
        if (task == null) throw new AppException("存在するタスクコードを入力してください");
        // タスクのステータスが、変更後のステータスの1つ前の場合
        if (task.getStatus() + 1 != updateStatus ) throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");

        // TaskDataAccess#updateでステータスを更新する
        task.setStatus(updateStatus);
        taskDataAccess.update(task);

        // ログデータを更新する
        Log log = new Log(task.getCode(), loginUser.getCode(), updateStatus, LocalDate.now());
        logDataAccess.save(log);

        System.out.println("ステータスの変更が完了しました。");
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    public void delete(int code) throws AppException {
        // codeからタスクデータを取得する

        // 入力されたタスクコードが存在しない場合

        // 該当タスクのステータスが、完了（2）ではない場合

        // TaskLogic#deleteでタスクの削除

        // タスクに紐づくログの削除
    }
}