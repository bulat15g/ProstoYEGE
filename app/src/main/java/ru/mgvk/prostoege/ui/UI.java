package ru.mgvk.prostoege.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.mgvk.prostoege.*;
import ru.mgvk.prostoege.ru.mgvk.prostoege.fragments.ExercisesListFragment;
import ru.mgvk.prostoege.ru.mgvk.prostoege.fragments.TaskListFragment;
import ru.mgvk.prostoege.ru.mgvk.prostoege.fragments.ToolsFragment;
import ru.mgvk.prostoege.ru.mgvk.prostoege.fragments.VideoListFragment;
import ru.mgvk.util.Reporter;

/**
 * Created by Michael_Admin on 08.08.2016.
 */
public class UI {

    private static int realDPI;
    public int deviceWidth = 0, deviceHeight = 0;

    public View rootView0, rootView1, rootView2;
    public MainScrollView mainScroll;
    public MainMenu mainMenu;
    //    ImageButton backButton;
    public TaskListFragment taskListFragment;
    public VideoListFragment videoListFragment;
    public Fragment currentFragment, previosFragment;
    public ExercisesListFragment exercisesListFragment;
    public ToolsFragment toolsFragment;
    Context context;
    MainActivity mainActivity;
    FragmentTransaction tr;
    FragmentManager manager;
    boolean added = true;
    private BalanceWindow balanceWindow;

    public UI(Context context, boolean restoring) {


        mainActivity = (MainActivity) (this.context = context);

        mainActivity.stopwatch.checkpoint("UI_start");

        realDPI = mainActivity.getResources().getDisplayMetrics().densityDpi;

        if (!DataLoader.isLicenseAccepted(context)) {
            openPolicyWindow();
        }

        initSizes();
        initFragments(restoring);
//        addFragments();

        initViews();
        updateSizes(context.getResources().getConfiguration().orientation);

        openTaskListFragment();
        mainActivity.stopwatch.checkpoint("UI_finish");

    }

    public static float calcSize(float size) {
        return (float) (size * (realDPI / (double) 160));
    }

    public static int calcSize(int size) {
        return (int) (size * (realDPI / (double) 160));
    }

    public static int calcFontSize(int size) {
        return (int) (size * (160 / (double) realDPI));
    }

    public static void enterFullScreen(Context context) {

        ((MainActivity) context).findViewById(R.id.root_0)
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        try {
            ((MainActivity) context).getActionBar().hide();
        } catch (NullPointerException ignored) {
        }
    }

    public static void exitFullScreen(Context context) {

        ((MainActivity) context).findViewById(R.id.root_0)
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_VISIBLE);
        try {
            ((MainActivity) context).getActionBar().show();
        } catch (NullPointerException ignored) {
        }
    }

    public static String getPriceLabel(int price) {
        String s;
        int p = price % 10;
        if (p == 1) {
            s = " Ёж";
        } else if (p > 1 && p < 5) {
            s = " Ежа";
        } else {
            s = " Ежей";
        }

        return price + s;

    }

    public static void makeErrorMessage(Context context, String s) {

        HintWindow window = new HintWindow(context);
        TextView text = new TextView(context);
        text.setText(s);
        window.layout.setBackgroundResource(R.drawable.answer_incorrect);
        text.setTextColor(Color.WHITE);
        window.addView(text);

        window.open();
    }

    private void openPolicyWindow() {

        WebView webView = new WebView(context);
        webView.loadUrl(DataLoader.PolicyURL);
        webView.clearCache(true);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
//        webView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {}
//        });

        new AlertDialog.Builder(context)
                .setMessage("Пользовательское соглашение")
                .setCancelable(false)
                .setView(webView)
                .setPositiveButton("Принимаю", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DataLoader.acceptLicense(context);
                        } catch (Exception e) {
                            Reporter.report(context, e, ((MainActivity) context).reportSubject);
                        }
                    }
                })
                .setNegativeButton("Не принимаю", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mainActivity.finish();
                        } catch (Exception e) {
                            Reporter.report(context, e, ((MainActivity) context).reportSubject);
                        }
                    }
                }).create().show();


//        LicenseWindow window = new LicenseWindow(context);
//        window.open();

    }

    void initSizes() {
        deviceWidth = mainActivity.getResources().getDisplayMetrics().widthPixels;
        deviceHeight = mainActivity.getResources().getDisplayMetrics().heightPixels;
    }

    void initViews() {
        rootView0 = mainActivity.findViewById(R.id.root_0);
        rootView1 = mainActivity.findViewById(R.id.root_1);
        rootView2 = mainActivity.findViewById(R.id.root_2);
        mainScroll = (MainScrollView) mainActivity.findViewById(R.id.mainScroll);
        mainScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);

        initMainMenu();

    }

    void initFragments(boolean restoring) {
        if (restoring) {
//            taskListFragment = (TaskListFragment) mainActivity.getFragmentManager()
//                    .findFragmentByTag("TaskListFragment");
//            videoListFragment = (VideoListFragment) mainActivity.getFragmentManager()
//                    .findFragmentByTag("VideoListFragment");
//            exercisesListFragment = (ExercisesListFragment) mainActivity.getFragmentManager()
//                    .findFragmentByTag("ExercisesListFragment");
//            toolsFragment = (ToolsFragment) mainActivity.getFragmentManager()
//                    .findFragmentByTag("ToolsFragment");
            taskListFragment = (TaskListFragment) InstanceController.getObject("TasksFragment");
            videoListFragment = (VideoListFragment) InstanceController.getObject("VideosFragment");
            exercisesListFragment = (ExercisesListFragment) InstanceController.getObject("ExercisesFragment");
            toolsFragment = (ToolsFragment) InstanceController.getObject("ToolsFragment");
        } else {
            taskListFragment = new TaskListFragment(context);
            videoListFragment = new VideoListFragment(context);
            exercisesListFragment = new ExercisesListFragment(context);
            toolsFragment = new ToolsFragment(context);

            try {
                InstanceController.putObject("TasksFragment", taskListFragment);
            } catch (InstanceController.NotInitializedError notInitializedError) {
                notInitializedError.printStackTrace();
            }
            try {
                InstanceController.putObject("VideosFragment", videoListFragment);
            } catch (InstanceController.NotInitializedError notInitializedError) {
                notInitializedError.printStackTrace();
            }
            try {
                InstanceController.putObject("ExercisesFragment", exercisesListFragment);
            } catch (InstanceController.NotInitializedError notInitializedError) {
                notInitializedError.printStackTrace();
            }
            try {
                InstanceController.putObject("ToolsFragment", toolsFragment);
            } catch (InstanceController.NotInitializedError notInitializedError) {
                notInitializedError.printStackTrace();
            }

        }
    }

    void initMainMenu() {
        mainMenu = new MainMenu(context, (ViewGroup) rootView0);

    }

    public void openMenu(MenuPanel menu) {
        if (menu != null) {
            doMenuAppearAnimation(menu);
//            menu.setX(0);
//            menu.setVisibility(View.VISIBLE);
        }
    }

    public void closeMenu(MenuPanel menu) {
        if (menu != null) {
            doMenuDisappearAnimation(menu);
        }
    }

    void doMenuAppearAnimation(final MenuPanel menu) {
        menu.setX(-1 * menu.getWidth());
        menu.setVisibility(View.VISIBLE);
        ObjectAnimator a = ObjectAnimator.ofFloat(menu, "x", menu.getX(), 0);
        a.setDuration(300);
        a.start();

        mainActivity.addToBackStack(new Runnable() {
            @Override
            public void run() {
                closeMenu(menu);
            }
        });

    }

    void doMenuDisappearAnimation(final MenuPanel menu) {
        ObjectAnimator a = ObjectAnimator.ofFloat(menu, "x", 0, -1 * menu.getWidth());
        a.setDuration(300);
        a.start();
        a.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                menu.setVisibility(View.INVISIBLE);
                menu.setX(-1 * menu.getWidth());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void updateSizes(int orientation) {
        initSizes();
        double k = orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 0.5;
        rootView1.setLayoutParams(new LinearLayout.LayoutParams((int) (k * deviceWidth), -1));
        rootView2.setLayoutParams(new LinearLayout.LayoutParams((int) (k * deviceWidth), -1));

        mainMenu.setLayoutParams(new FrameLayout.LayoutParams((deviceWidth), -1));
        mainMenu.updateSizes((int) (k * deviceWidth), deviceHeight);
    }

    public void openTaskOrVideoFragment(final boolean task) {

        manager = mainActivity.getFragmentManager();
        tr = manager.beginTransaction();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (manager.findFragmentById(videoListFragment.getId()) == null) {
                    added = false;
                    tr.add(R.id.root_2, videoListFragment, "VideoListFragment");
                    added = true;
                }
            }
        }).start();

        if (manager.findFragmentById(taskListFragment.getId()) == null) {
            tr.add(R.id.root_1, taskListFragment, "TaskListFragment");
        }

        while (!added) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        if (currentFragment != null) {
            hideCurrentFragments(tr, false);
        }

        tr.show(taskListFragment);

        tr.show(videoListFragment);

        tr.commit();

        currentFragment = taskListFragment;

        if (task) {
            mainScroll.toLeft();
        } else {
            mainScroll.toRight();
        }

    }

    private void addFragments() {
        FragmentManager manager = mainActivity.getFragmentManager();
        FragmentTransaction tr = manager.beginTransaction();
        tr.add(R.id.root_1, exercisesListFragment, "ExercisesListFragment");
        tr.add(R.id.root_2, toolsFragment, "ToolsFragment");
        tr.add(R.id.root_2, videoListFragment, "VideoListFragment");
        tr.add(R.id.root_1, taskListFragment, "TaskListFragment");

        tr.hide(taskListFragment);
        tr.hide(exercisesListFragment);
        tr.hide(videoListFragment);
        tr.hide(toolsFragment);
        tr.commit();


    }

    public void openExercisesOrToolsFragment(final boolean exercises) {
        FragmentManager manager = mainActivity.getFragmentManager();
        FragmentTransaction tr = manager.beginTransaction();

        if (manager.findFragmentById(exercisesListFragment.getId()) == null) {
            tr.add(R.id.root_1, exercisesListFragment, "ExercisesListFragment");
        }
        if (manager.findFragmentById(toolsFragment.getId()) == null) {
            tr.add(R.id.root_2, toolsFragment, "ToolsFragment");
        }

        if (currentFragment != null) {
            hideCurrentFragments(tr, true);
        }

        tr.show(exercisesListFragment);
        tr.show(toolsFragment);
        tr.commit();
        currentFragment = exercisesListFragment;
        if (exercises) {
            mainScroll.toLeft();
        } else {
            mainScroll.toRight();
        }


    }

    public void openTaskListFragment() {

        openTaskOrVideoFragment(true);
    }

    public void hideCurrentFragments(FragmentTransaction tr, boolean currIsTasks) {
        if (currIsTasks) {
            tr.hide(taskListFragment);
            tr.hide(videoListFragment);
        } else {
            tr.hide(toolsFragment);
            tr.hide(exercisesListFragment);
        }
    }

    public void setCurrentTask(Task task) throws Exception {
        videoListFragment.setCurrentTask(task);
        if (exercisesListFragment == null) {
            exercisesListFragment = new ExercisesListFragment(context);
        }
        exercisesListFragment.setCurrentTask(task);
        if (toolsFragment == null) {
            toolsFragment = new ToolsFragment(context);
        }
        toolsFragment.setTask(task);
    }

    public void openVideoListFragment(Task task) throws Exception {

        setCurrentTask(task);


        openTaskOrVideoFragment(false);

        mainActivity.addToBackStack(new Runnable() {
            @Override
            public void run() {
                openTaskOrVideoFragment(true);
            }
        });
    }

    public void openExercisesListFragment() {

        openExercisesOrToolsFragment(true);

        mainActivity.addToBackStack(new Runnable() {
            @Override
            public void run() {
                openTaskOrVideoFragment(false);
            }
        });
    }

    public void openToolsFragment() {

        openExercisesOrToolsFragment(false);
        mainActivity.addToBackStack(new Runnable() {
            @Override
            public void run() {
                openExercisesOrToolsFragment(true);
            }
        });

    }

    public void openPreviousFragment() {
        FragmentTransaction tr = mainActivity.getFragmentManager().beginTransaction();
        tr.hide(currentFragment);
        tr.commit();
    }

    public void openBalanceDialog() {
        balanceWindow = new BalanceWindow(context);
        balanceWindow.open();
    }

    public void openHintWindow(Task.Exercise currentExercise) {

        if (currentExercise.isPromted()||currentExercise.isSolved()) {
            HintWindow hintWindow = new HintWindow(context);
            hintWindow.addView(new HintWebView(context, currentExercise.getHintID()));
            hintWindow.open();
        } else {
            openHintPurchaseWindow(currentExercise);
        }
    }

    public void openHintPurchaseWindow(final Task.Exercise exercise) {
        final HintWindow window = new HintWindow(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(-2, calcSize(80)));

        TextView txt = new TextView(context);
        txt.setText("Подсказка стоит "
                + getPriceLabel(exercise.getHintPrice()) +
                "\nПродолжить?\n" +
                "У Вас " + getPriceLabel(mainActivity.profile.Coins));
        txt.setTextSize(20);
        txt.setTextColor(Color.WHITE);
        txt.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        layout.addView(txt);

        ImageButton okBtn = new ImageButton(context);
        okBtn.setBackgroundResource(R.drawable.ok);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(UI.calcSize(40), UI.calcSize(40));
        lp.gravity = Gravity.RIGHT;
        okBtn.setLayoutParams(lp);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mainActivity.onBackPressed();
                    if (mainActivity.profile.Coins < exercise.getHintPrice()) {
                        openLowCoinsWindow();
                    } else {
                        if (mainActivity.pays.buyHint(exercise.getHintID())) {
                            mainActivity.updateCoins(-1 * exercise.getHintPrice());
                            updateCoins();
                            exercisesListFragment.getExerciseWindow().setStatus(ExerciseWindow.PROMPTED);
                            openHintWindow(exercise);
                        } else {
                            makeErrorMessage(context, "Произошла ошибка:(\nПопробуйте еще раз.");
                        }
                    }
                } catch (Exception e) {
                    Reporter.report(context, e, ((MainActivity) context).reportSubject);
                }
            }
        });
        layout.addView(okBtn);
        mainActivity.pays.setOnPurchaseListener(new Pays.OnPurchaseListener() {
            @Override
            public void OnPurchase() {
                window.close();
            }
        });
        window.addView(layout);
//        window.layout.getLayoutParams().height=-2;
//        window.layout.setLayoutParams(window.layout.getLayoutParams());
        window.open();


    }

    public void openVideoPurchaseDialog(Task.Video video) {
        final VideoPurchaseWindow videoPurchaseWindow = new VideoPurchaseWindow(context, video);
        videoPurchaseWindow.open();
    }

    public void openLowCoinsWindow() {
        final HintWindow hintWindow = new HintWindow(context);
        hintWindow.layout.setBackgroundResource(R.drawable.answer_incorrect);

        LinearLayout balanceLayout = new LinearLayout(context);
        balanceLayout.setOrientation(LinearLayout.VERTICAL);
        balanceLayout.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));

        TextView text = new TextView(context);
        text.setText("Не хватает ежей!");
        text.setTextSize(15);
        text.setTextColor(Color.WHITE);
        text.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        TextView balanceBtn = new TextView(context);
        balanceBtn.setBackgroundResource(R.drawable.btn_videodonate_balance);
        balanceBtn.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        balanceBtn.setText("ПОПОЛНИТЬ СЧЕТ");
        balanceBtn.setGravity(Gravity.CENTER);
        balanceBtn.setTextSize(15);
        balanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((MainActivity) context).ui.openBalanceDialog();
                } catch (Exception e) {
                    Reporter.report(context, e, ((MainActivity) context).reportSubject);
                }
            }
        });

        balanceLayout.addView(text);
        balanceLayout.addView(balanceBtn);

        hintWindow.addView(balanceLayout);
//        ((ViewGroup.LayoutParams) hintWindow.layout.getLayoutParams()).gravity = Gravity.CENTER;

        hintWindow.open();

    }

    public void openExerciseAnswerShowWindow(String answer) {
        final HintWindow window = new HintWindow(context);
        TextView txt = new TextView(context);
        txt.setTextColor(Color.WHITE);
        txt.setText("Верный ответ: " + answer);
        txt.setTextSize(20);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.setMargins(UI.calcSize(5), UI.calcSize(5), UI.calcSize(5), UI.calcSize(5));
        txt.setLayoutParams(lp);
        window.addView(txt);
        window.open();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        window.close();
                    }
                });
            }
        }).start();
    }

    public void openExerciseResultWindow(boolean correct) {
        final HintWindow window = new HintWindow(context);
        TextView txt = new TextView(context);
        txt.setTextColor(Color.WHITE);
        if (correct) {
            txt.setText("Верный ответ!");
            window.layout.setBackgroundResource(R.drawable.answer_correct);
        } else {
            txt.setText("Ответ неверен!");
            window.layout.setBackgroundResource(R.drawable.answer_incorrect);
        }
        txt.setTextSize(20);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.setMargins(UI.calcSize(5), UI.calcSize(5), UI.calcSize(5), UI.calcSize(5));
        txt.setLayoutParams(lp);
        window.addView(txt);
        window.open();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        window.close();
                    }
                });
            }
        }).start();
    }

    public void updateCoins() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    taskListFragment.updateCoins();
                } catch (Exception e) {
                    Reporter.report(context, e, ((MainActivity) context).reportSubject);
                }
            }
        });

    }

    public void openWifiOnlyDialog() {
        makeErrorMessage(context, "Включен режим \"Только WiFi\"!");
    }

    public void makeShareHelpMessage() {


        final HintWindow window = new HintWindow(context);
        window.layout.setBackgroundResource(R.drawable.hint_back);

        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        layout.setMinimumWidth(UI.calcSize(160));
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView text = new TextView(context);
        text.setText(R.string.help_share);
        text.setTextColor(Color.WHITE);
        text.setTextSize(17);
        layout.addView(text);

        TextView okBtn = new TextView(context);
        okBtn.setBackgroundResource(R.drawable.btn_answer_check);
        okBtn.setText("ЗАЙТИ!");
        okBtn.setTextSize(20);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2,-2);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        okBtn.setLayoutParams(lp);

        View.OnClickListener listener;
        okBtn.setOnClickListener(listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mainActivity.onBackPressed();
                    window.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        openTaskOrVideoFragment(true);
                                    } catch (Exception e) {
                                        Reporter.report(context, e, ((MainActivity) context).reportSubject);
                                    }
                                }
                            });
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        openMenu(mainMenu);
                                    } catch (Exception e) {
                                        Reporter.report(context, e, ((MainActivity) context).reportSubject);
                                    }
                                }
                            });
                        }
                    }).start();
                } catch (Exception e) {
                    Reporter.report(context, e, ((MainActivity) context).reportSubject);
                }
            }
        });
        layout.setOnClickListener(listener);
        layout.addView(okBtn);
        window.addView(layout);
//        window.layout.getLayoutParams().height=-2;
//        window.layout.setLayoutParams(window.layout.getLayoutParams());
        window.open();

    }

    public void onLeave() {
        try {
            videoListFragment.stopVideos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
