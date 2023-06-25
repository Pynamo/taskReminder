package com.example.taskReminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO 画像アップロード機能追加
// FIXME 一括削除機能追加
// TODO バッチ処理作成
// TODO ユーザー新規作成後自動ログイン機能追加
// TODO SNSログイン機能追加
// TODO Controllerの共通テスト作成
// TODO Repositoryの単体テスト作成
// TODO 詳細画面のMetabase連携
// FIXME ログアウト機能追加
// TODO　言語変更機能追加
// TODO ドキュメントまとめ
// TODO 更新画面の文章を可変にする
// FIXME CSSが見つからない問題の解決

@SpringBootApplication
public class TaskReminderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskReminderApplication.class, args);
	}

}
