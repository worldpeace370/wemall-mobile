package com.inuoer.util;

import com.inuoer.fragment.MainFragment;

import java.util.HashMap;
import java.util.Map;

/** MainFragment工厂类
 * Created by wuxiangkun on 2016/4/7 19:37.
 * Contacts wuxiangkun@live.com
 */
public class FragmentFactory {
    public static final String FRAGEMENT_ALL = "0";
    public static final String FRAGEMENT_HUOGUO = "1";
    public static final String FRAGEMENT_KAOROU = "2";
    public static final String FRAGEMENT_TAINDIAN = "3";
    public static final String FRAGEMENT_SHUANCHUAN = "4";

    private static Map<String, MainFragment> mFragmentCahce = new HashMap<>();

    public static MainFragment createFragment(String menu_id){
        MainFragment mainFragment = mFragmentCahce.get(menu_id);
        if (mainFragment == null){
            switch (menu_id){
                case FRAGEMENT_ALL:
                    mainFragment = MainFragment.newInstance(Config.API_GET_GOODS, null);
                    break;
                case FRAGEMENT_HUOGUO:
                    mainFragment = MainFragment.newInstance(Config.API_GET_GOODS, menu_id);
                    break;
                case FRAGEMENT_KAOROU:
                    mainFragment = MainFragment.newInstance(Config.API_GET_GOODS, menu_id);
                    break;
                case FRAGEMENT_TAINDIAN:
                    mainFragment = MainFragment.newInstance(Config.API_GET_GOODS, menu_id);
                    break;
                case FRAGEMENT_SHUANCHUAN:
                    mainFragment = MainFragment.newInstance(Config.API_GET_GOODS, menu_id);
                    break;
            }
            mFragmentCahce.put(menu_id, mainFragment);
        }
        return mainFragment;
    }

    public static void clear(){
        mFragmentCahce.clear();
    }
}
