package com.example.lab5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MainScene extends Activity implements View.OnClickListener {
    TextView userName;
    TextView out;
    Button exit;
    Button deleteUser;
    Button changePassword;
    Button accept;
    EditText newPassword;
    TextView errorMessage;
    Thread bb;
    Intent intent;
    boolean fl = false;

    DatabaseHandler db = new DatabaseHandler(this);
    User user = new User();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_scene);
        userName = findViewById(R.id.userName);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null){
            user = (User) arguments.getSerializable("user");
        }
        userName.setText(user.getLogin());

        out = findViewById(R.id.out);
        out.setVisibility(View.INVISIBLE);

        exit = findViewById(R.id.exitButton);

        exit.setOnClickListener(this);
        deleteUser = findViewById(R.id.deleteUserButton);
        deleteUser.setOnClickListener(this);
        changePassword = findViewById(R.id.changePassword);
        changePassword.setOnClickListener(this);
        accept = findViewById(R.id.acceptNewPassword);
        accept.setOnClickListener(this);

        newPassword = findViewById(R.id.newPassword);
        errorMessage = findViewById(R.id.errorNewPassword);

        intent = new Intent(this, InitScene.class);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.exitButton:
                startActivity(intent);
                break;

            case R.id.deleteUserButton:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.deleteUserFromDB(user);
                        db.close();
                    }
                }).start();
                startActivity(intent);
                break;

            case R.id.changePassword:
                newPassword.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
                break;

            case R.id.acceptNewPassword:

                String newPwd = newPassword.getText().toString();
                System.out.println("new " + newPwd);
                System.out.println(user.getPass());
                if(newPwd.equals("")){
                    errorMessage.setText("Enter your new password !");
                    errorMessage.setVisibility(View.VISIBLE);
                }else if(newPwd.equals(user.getPass())){
                    errorMessage.setText("Passwords match!");
                    errorMessage.setVisibility(View.VISIBLE);
                }else {
                    errorMessage.setText("Password was successfully changed.\n");
                    errorMessage.setVisibility(View.VISIBLE);
                    newPassword.setVisibility(View.INVISIBLE);
                    accept.setVisibility(View.INVISIBLE);

                    out.setVisibility(View.VISIBLE);
                    String mes = "*";
                    out.setText(mes);

                   new Thread(new Runnable() {
                        @Override
                        public void run() {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    db.updateUserPassword(user, newPwd);
                                    db.close();
                                }
                            }, 15000);




                            /*try {
                                Thread.currentThread().sleep(15000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                            fl = true;
                        }
                   }).start();


                    while(!fl){
                        String finalMes = mes;
                        out.post(new Runnable() {
                            @Override
                            public void run() {
                                out.setText(finalMes);
                            }
                        });
                        out.setText(mes);
                        out.setVisibility(View.VISIBLE);
                        System.out.println(mes);
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mes +="*";
                    }


                }
                break;

            default:
                break;
        }
    }
}
