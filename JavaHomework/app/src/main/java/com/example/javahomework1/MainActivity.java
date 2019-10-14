/*    时间：2019/10/01
 *    作者：南开大学1613415 catnip
 *    注释：2019年秋JAVA作业1
 * */
package com.example.javahomework1;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import static com.example.javahomework1.baseClass.ACTION.EM_FLOW;
import static com.example.javahomework1.baseClass.ACTION.EM_SMSCOUNT;
import static com.example.javahomework1.baseClass.ACTION.EM_TALKTIME;
import static com.example.javahomework1.baseClass.card;
import static com.example.javahomework1.baseClass.scene;

//一级功能
public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //预存用户信息和场景信息
        SaveInfoAtFirst();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void SaveInfoAtFirst()
    {
        //预存用户信息1
        baseClass.MobileCard card1 = new baseClass().new MobileCard();
        card1.UserName = "Catnip";
        card1.PassWord = "3344";
        card1.CardNumber = "13911223344";
        card1.SerPackage = new baseClass().new SuperPackage();
        card1.ConsumAmount = 78;
        card1.Money = 1;
        card.put(card1.CardNumber, card1);

        //预存用户信息2
        baseClass.MobileCard card2 = new baseClass().new MobileCard();
        card2.UserName = "David";
        card2.PassWord = "2211";
        card2.CardNumber = "13944332211";
        card2.SerPackage = new baseClass().new NetPackage();
        card2.ConsumAmount = 68;
        card2.Money = 50;
        card.put(card2.CardNumber, card2);

        //预存6个场景信息
        String detail = "问候客户，谁知其如此难缠，通话90分钟";
        baseClass.Scene scene1 = new baseClass().new Scene(detail, EM_TALKTIME, 90, 18);
        scene.put(1, scene1);

        detail = "询问妈妈身体情况，通话30分钟";
        baseClass.Scene scene2 = new baseClass().new Scene(detail, EM_TALKTIME, 30, 6);
        scene.put(2, scene2);

        detail = "通知朋友手机换号，发送短信50条";
        baseClass.Scene scene3 = new baseClass().new Scene(detail, EM_SMSCOUNT, 50, 5);
        scene.put(3, scene3);

        detail = "参与环境保护实施方案问卷调查，发送短信5条";
        baseClass.Scene scene4 = new baseClass().new Scene(detail, EM_SMSCOUNT, 5, 0.5);
        scene.put(4, scene4);

        detail = "上网查资料，使用流量100MB";
        baseClass.Scene scene5 = new baseClass().new Scene(detail, EM_FLOW, 100, 10);
        scene.put(5, scene5);

        detail = "提交JAVA作业，使用流量300MB";
        baseClass.Scene scene6 = new baseClass().new Scene(detail, EM_FLOW, 300, 60);
        scene.put(6, scene6);
    }

    //*********************** 注册相关 **************************
    // 获得指定位数的随机数
    public static String getRandom(int len)
    {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
            rs.append(r.nextInt(10));
        }
        return rs.toString();
    }
    // 用户注册步骤1：9个随机号码
    public void Reg_Show9Number(){
        final String[] saveNumber = new String [9]; // 生成9个随机号码
        for(int i = 0; i < 9; i = i + 1) {
            String number;
            while(true) {
                number = "139" + getRandom(8);
                if(!card.containsKey(number)) //合法性判断，是否已经注册
                    break;
            }
            saveNumber[i] = number;
        }
        final baseClass.MobileCard newCard = new baseClass().new MobileCard();
        AlertDialog.Builder NumberDiaglog = new AlertDialog.Builder(MainActivity.this);
        NumberDiaglog.setTitle("请选择你要注册的手机号码"); //标题
        NumberDiaglog.setSingleChoiceItems(saveNumber, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface number_dialog, int which) { //选择手机号
                        newCard.CardNumber = saveNumber[which];
                    }});
        NumberDiaglog.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface next_dialog, int which) {
                        Reg_ChoosePackage(newCard); //下一步，选择套餐
                    }});
        NumberDiaglog.setNegativeButton("取消", null);
        NumberDiaglog.show();
    }
    // 用户注册步骤2：选择套餐
    public void Reg_ChoosePackage(final baseClass.MobileCard newCard){
        AlertDialog.Builder PackageDialog = new AlertDialog.Builder(MainActivity.this);
        PackageDialog.setTitle("请选择你要办理的套餐"); //标题
        String[] savePackage = {"话痨套餐", "超人套餐", "网虫套餐"};
        PackageDialog.setSingleChoiceItems(savePackage, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface package_dialog, int which) { //选择套餐
                        baseClass.ServicePackage temp_package;
                        if(which == 0)
                            temp_package = new baseClass().new TalkPackage();
                        else if(which == 1)
                            temp_package = new baseClass().new SuperPackage();
                        else
                            temp_package = new baseClass().new NetPackage();
                        newCard.SerPackage = temp_package;
                    }});
        PackageDialog.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface next_dialog, int which) {
                        FullfillName(newCard); //下一步，填写姓名
                        next_dialog.dismiss();
                    }});
        PackageDialog.setNegativeButton("取消", null);
        PackageDialog.show();
    }
    // 用户注册步骤3：填写姓名密码 + 充值
    public void FullfillName(final baseClass.MobileCard newCard)
    {
        AlertDialog.Builder InfoDiaglog = new AlertDialog.Builder(MainActivity.this);
        LinearLayout layout=new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        InfoDiaglog.setTitle("请输入你的信息"); //标题

        final EditText name = new EditText(MainActivity.this);
        name.setHint("请输入姓名");
        final EditText password = new EditText(MainActivity.this);
        password.setHint("请输入密码");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText money = new EditText(MainActivity.this);
        money.setHint("请输入预存话费");
        money.setInputType(InputType.TYPE_CLASS_NUMBER);

        layout.addView(name);
        layout.addView(password);
        layout.addView(money);

        InfoDiaglog.setView(layout);
        InfoDiaglog.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface judge_dialog, int which) {
                        String GetName     = name.getText().toString();
                        String GetPassword = password.getText().toString();
                        String GetMoney    = money.getText().toString();
                        if(GetName.isEmpty() || GetPassword.isEmpty() || GetMoney.isEmpty())
                        {
                            AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                            result.setTitle("错误");
                            result.setMessage("注册失败：姓名/密码/预存金额 不能为空！");
                            result.show();
                            return ;
                        }
                        double d_GetMoney = Double.parseDouble(GetMoney);
                        if(d_GetMoney < 50) //充值金额少于50元
                        {
                            AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                            result.setTitle("错误");
                            result.setMessage("注册失败：充值金额需大于50元！");
                            result.show();
                            return ;
                        }
                        if(d_GetMoney < newCard.SerPackage.price) //余额不足
                        {
                            AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                            result.setTitle("错误");
                            result.setMessage("注册失败：您预存的话费不足以支付本月固定套餐经费，请重新充值！");
                            result.show();
                            return ;
                        }

                        newCard.UserName     = GetName;
                        newCard.PassWord     = GetPassword;
                        newCard.ConsumAmount = newCard.SerPackage.price; //已消费总金额
                        newCard.Money        = d_GetMoney - newCard.SerPackage.price; //当前余额

                        card.put(newCard.CardNumber, newCard);

                        AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                        result.setTitle("注册成功");
                        String content = "卡号：" + newCard.CardNumber + "\n" +
                                         "用户名：" + newCard.UserName + "\n" +
                                         "当前余额：" + newCard.Money  + "\n";
                        if(newCard.SerPackage.GetPackageName().equals("话痨套餐"))
                            content += "话痨套餐：通话时长为 120 分钟/月，短信条数为 50 条/月";
                        else if(newCard.SerPackage.GetPackageName().equals("超人套餐"))
                            content += "超人套餐：通话时长为 200 分钟/月，短信条数为 50 条/月，上网流量为 1 GB/月";
                        else if(newCard.SerPackage.GetPackageName().equals("网虫套餐"))
                            content += "网虫套餐：上网流量为 5 GB/月";
                        result.setMessage(content);
                        result.show();
                    }});

        InfoDiaglog.show();
    }
    //*************************************************************

    //加载fare.txt
    public String LoadFareFile()
    {
        String result = "";
        try {
            //按行读取 + 解决中文乱码
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("fare.txt"),"GB2312");
            if (inputReader != null) {
                BufferedReader buffreader = new BufferedReader(inputReader);
                String line = "";
                while ((line = buffreader.readLine()) != null) {
                    result = result + line + "\n";
                }
                inputReader.close();
            }
        } catch (Exception e) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("错误");
            dialog.setMessage("执行出错！");
            dialog.show();
        }
        return result;
    }

    //1、用户登录
    public void UsrLogin(View v)
    {
        AlertDialog.Builder InfoDiaglog = new AlertDialog.Builder(MainActivity.this);
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        InfoDiaglog.setTitle("请输入你的信息"); //标题

        final EditText number = new EditText(MainActivity.this);
        number.setInputType(InputType.TYPE_CLASS_NUMBER);
        number.setHint("请输入手机号");
        final EditText password = new EditText(MainActivity.this);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint("请输入密码");

        layout.addView(number);
        layout.addView(password);
        InfoDiaglog.setView(layout);

        InfoDiaglog.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface judge_dialog, int which) {
                    final String GetNumber = number.getText().toString();
                    String GetPassword = password.getText().toString();
                    if(!card.containsKey(GetNumber)) //合法性判断，是否已经注册
                    {
                        AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                        result.setTitle("错误");
                        if(GetNumber.isEmpty()) {
                            result.setMessage("登录失败：当前账号不能为空！");
                        }
                        else {
                            result.setMessage("登录失败：当前账号尚未注册！");
                        }
                        result.show();
                    }
                    else
                    {
                        if (!GetPassword.equals(card.get(GetNumber).PassWord)) {
                            AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                            result.setTitle("错误");
                            result.setMessage("登录失败：密码错误！");
                            result.show();
                        } else {
                            AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                            result.setTitle("成功");
                            result.setMessage("登录成功");
                            result.setPositiveButton("下一步",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface judge_dialog, int which) {
                                            Intent MyIntent = new Intent(MainActivity.this,SecondActivity.class);
                                            MyIntent.putExtra("number", GetNumber); //参数为用户手机号
                                            startActivityForResult(MyIntent,1); // 启动Activity
                                        }
                                    });
                            result.show();
                        }
                    }
                }});
        InfoDiaglog.show();
    }

    //2、用户注册
    public void UsrRegister(View v)
    {
        Reg_Show9Number();
    }

    //3、使用嗖嗖
    public void UseSoso(View v)
    {
        AlertDialog.Builder InfoDiaglog = new AlertDialog.Builder(MainActivity.this);
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        InfoDiaglog.setTitle("使用嗖嗖");
        final EditText number = new EditText(MainActivity.this);
        number.setInputType(InputType.TYPE_CLASS_NUMBER);
        number.setHint("请输入手机号");
        layout.addView(number);
        InfoDiaglog.setView(layout);
        InfoDiaglog.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface judge_dialog, int which) {
                        final String GetNumber = number.getText().toString();
                        // 查看该号码是否被注册过
                        if(!card.containsKey(GetNumber)) //合法性判断，是否已经注册
                        {
                            AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                            result.setTitle("错误");
                            if(GetNumber.isEmpty())
                                result.setMessage("使用嗖嗖失败：账号不能为空！");
                            else
                                result.setMessage("使用嗖嗖失败：当前账号尚未注册！");
                            result.show();
                            return ;
                        }
                        AlertDialog.Builder diaglog = new AlertDialog.Builder(MainActivity.this);
                        diaglog.setTitle("使用嗖嗖");
                        int random = new Random().nextInt(6) + 1; //生成[1, 6]的随机数
                        if(random == 1 || random == 2) { //通话相关，涉及超人和话痨
                            CallService temp_interface = new baseClass().new SuperPackage(); //先初始化为超人
                            if (card.get(GetNumber).SerPackage instanceof baseClass.TalkPackage) //如果是话痨，再改成话痨
                                temp_interface = new baseClass().new TalkPackage();
                            temp_interface.call(diaglog, card.get(GetNumber), random);
                        }
                        else if(random == 3 || random == 4){ //短信相关，涉及超人和话痨
                            SendService temp_interface = new baseClass().new SuperPackage(); //先初始化为超人
                            if (card.get(GetNumber).SerPackage instanceof baseClass.TalkPackage) //如果是话痨，再改成话痨
                                temp_interface = new baseClass().new TalkPackage();
                            temp_interface.send(diaglog, card.get(GetNumber), random);
                        }
                        else{ //流量相关，涉及超人和网虫
                            NetService temp_interface = new baseClass().new SuperPackage(); //先初始化为超人
                            if (card.get(GetNumber).SerPackage instanceof baseClass.TalkPackage) //如果是网虫，再改成网虫
                                temp_interface = new baseClass().new NetPackage();
                            temp_interface.netPlay(diaglog, card.get(GetNumber), random);
                        }
                    }
                });
        InfoDiaglog.show();
    }

    //4、话费充值
    public void RechargePhoneBill(View v)
    {
        AlertDialog.Builder InfoDiaglog = new AlertDialog.Builder(MainActivity.this);
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        InfoDiaglog.setTitle("请输入你的信息"); //标题

        final EditText number = new EditText(MainActivity.this);
        number.setInputType(InputType.TYPE_CLASS_NUMBER);
        number.setHint("请输入手机号");
        final EditText password = new EditText(MainActivity.this);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint("请输入密码");
        final EditText money = new EditText(MainActivity.this);
        money.setInputType(InputType.TYPE_CLASS_NUMBER);
        money.setHint("请输入充值金额");

        layout.addView(number);
        layout.addView(password);
        layout.addView(money);
        InfoDiaglog.setView(layout);

        InfoDiaglog.setPositiveButton("下一步",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface judge_dialog, int which) {
                        String GetNumber = number.getText().toString();
                        String GetPassword = password.getText().toString();
                        String GetMoney = money.getText().toString();
                        if(!card.containsKey(GetNumber)) //合法性判断，是否已经注册
                        {
                            AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                            result.setTitle("错误");
                            if(GetNumber.isEmpty()) {
                                result.setMessage("充值失败：当前账号不能为空！");
                            }
                            else {
                                result.setMessage("充值失败：当前账号尚未注册！");
                            }
                            result.show();
                        }
                        else
                        {
                            if (!GetPassword.equals(card.get(GetNumber).PassWord)) {
                                AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                                result.setTitle("错误");
                                result.setMessage("充值失败：密码错误！");
                                result.show();
                            } else {
                                if(Double.parseDouble(GetMoney) < 50) //充值金额少于50元
                                {
                                    AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                                    result.setTitle("错误");
                                    result.setMessage("充值失败：充值金额需大于50元！");
                                    result.show();
                                }
                                else {
                                    AlertDialog.Builder result = new AlertDialog.Builder(MainActivity.this);
                                    result.setTitle("成功");
                                    card.get(GetNumber).Money = card.get(GetNumber).Money + Double.parseDouble(GetMoney);
                                    String content = "充值成功，当前账户余额为" + card.get(GetNumber).Money + "元";
                                    result.setMessage(content);
                                    result.show();
                                }
                            }
                        }
                    }});
        InfoDiaglog.show();
    }

    //5、资费说明
    public void ShowFare(View v) {
        AlertDialog.Builder FareDiaglog = new AlertDialog.Builder(MainActivity.this);
        FareDiaglog.setTitle("资费说明"); //标题
        String content = LoadFareFile();
        FareDiaglog.setMessage(content);
        FareDiaglog.setNegativeButton("返回", null);
        FareDiaglog.show();
    }

    //6、退出系统
    public void ExitSoso(View v)
    {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}


