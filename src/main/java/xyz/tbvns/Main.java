package xyz.tbvns;

import com.formdev.flatlaf.FlatDarculaLaf;
import oshi.SystemInfo;
import xyz.tbvns.Others.AdminUtils;
import xyz.tbvns.Windows.WelcomeWindow;

public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        new WelcomeWindow().show();
    }
}