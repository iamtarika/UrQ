package com.example.tarika.urq;

// class สำหรับ Listview การเพิ่มข้อมูล ลงไปใน Listview

public class ListSearchStore {

    private String name_shop;
    private String q_shop;

    public ListSearchStore(String name_shop, String q_shop){
        this.name_shop = name_shop; //รับค่าที่เป็นชื่อร้านค้า
        this.q_shop = q_shop;       //รับค่าที่เป็นเลขคิว

    }

    public String getName_shop() {
        return name_shop;
    }

    public String getQ_shop() {
        return q_shop;
    }

}
