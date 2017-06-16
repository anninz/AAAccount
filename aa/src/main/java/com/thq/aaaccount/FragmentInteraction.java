package com.thq.aaaccount;

/**
     * 定义了所有activity必须实现的接口
     */
    public interface FragmentInteraction {
        /**
         * Fragment 向Activity传递指令，这个方法可以根据需求来定义
         * @param str
         */
        void process(String str);
        String getActivityId();


    }