package com.kai.test;


import org.junit.jupiter.api.Test;


public class uploadTest {
    @Test
        public void test1(){

        String fileName="dakaizi.jpg";
        String substring = fileName.substring(fileName.lastIndexOf("."));

        System.out.println(substring);

    }


}
