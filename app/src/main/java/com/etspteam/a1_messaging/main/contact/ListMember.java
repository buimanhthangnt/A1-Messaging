package com.etspteam.a1_messaging.main.contact;

import com.etspteam.a1_messaging.R;

import java.util.ArrayList;
import java.util.List;

public class ListMember {
    private static List<Member> list;

    private ListMember() {
    }

    public static List<Member> getList() {
        if (list == null) {
            list = new ArrayList<>();
            list.add(new Member(1,"Đỗ Thị Lan Anh", R.drawable.lananh, "01674316490", "lananh", "2586", "L. Anh"));
            list.add(new Member(2, "Nguyễn Thị Phương Anh", R.drawable.phuonganh, "Unknown", "phuonganh", "8964", "P. Anh"));
            list.add(new Member(3, "Lưu Thị Chiều", R.drawable.chieu, "0946305628", "chieu", "4561", "Chiều"));
            list.add(new Member(4, "Trần Thị Dung", R.drawable.dung, "Unknown", "dung", "1389", "Dung"));
            list.add(new Member(5, "Phạm Văn Duy", R.drawable.duy, "Unknown", "duy", "4643", "Duy"));
            list.add(new Member(6, "Nguyễn Thị Duyên", R.drawable.duyen, "Unknown", "duyen", "7946", "Duyên"));
            list.add(new Member(7, "Hoàng Thùy Dương", R.drawable.duong, "01626073583", "duong", "1346", "Dương"));
            list.add(new Member(8, "Phạm Tuấn Đức", R.drawable.duc, "0974718197", "duc", "6587", "Đức"));
            list.add(new Member(9, "Đỗ Thị Én", R.drawable.en, "Unknown", "en", "6348", "Én"));
            list.add(new Member(10, "Đào Thu Hà", R.drawable.daoha, "01677972451", "daoha", "8461", "Đ. Hà"));
            list.add(new Member(11, "Lê Thị Hà", R.drawable.leha, "01234319699", "leha", "1123", "L. Hà"));
            list.add(new Member(12, "Lê Thị Hiền", R.drawable.lehien, "0971122297", "lehien", "7946", "Lê Hiền"));
            list.add(new Member(13, "Lưu Thị Hiền", R.drawable.luuhien, "Unknown", "luuhien", "2486", "Lưu Hiền"));
            list.add(new Member(14, "Trần Thị Hòa", R.drawable.hoa, "Unknown", "hoa", "5549", "Hòa"));
            list.add(new Member(15, "Hoàng Hải Huế", R.drawable.hoanghue, "Unknown", "hoanghue", "9946", "Huế"));
            list.add(new Member(16, "Trần Thị Huệ", R.drawable.tranhue, "01658947309", "tranhue", "2233", "Huệ"));
            list.add(new Member(17, "Bùi Thị Hương", R.drawable.huong, "0967549762", "huong", "0124", "Hương"));
            list.add(new Member(18, "Phạm Thu Lan", R.drawable.lan, "09774752962", "lan", "1089", "Lan"));
            list.add(new Member(19, "Bùi Thị Thùy Linh", R.drawable.linh, "01653972361", "linh", "2347", "Linh"));
            list.add(new Member(20, "Lê Thị Lý", R.drawable.ly, "01659984918", "ly", "2301", "Lý"));
            list.add(new Member(21, "Đặng Thị Hồng Ngọc", R.drawable.dangngoc, "01662837245", "dangngoc", "9044", "H. Ngọc"));
            list.add(new Member(22, "Đỗ Thị Ngọc", R.drawable.dongoc, "01686560362", "dongoc", "8460", "Đ. Ngọc"));
            list.add(new Member(23, "Lê Thị Nguyên", R.drawable.nguyen, "0985720480", "nguyen", "4053", "Nguyên"));
            list.add(new Member(24, "Lê Thị Nhàn", R.drawable.nhan, "Unknown", "nhan", "6000", "Nhàn"));
            list.add(new Member(25, "Đỗ Hồng Nhung", R.drawable.nhung, "Unknown", "nhung", "5610", "Nhung"));
            list.add(new Member(26, "Nguyễn Mai Như", R.drawable.nhu, "Unknown", "nhu", "3300", "Như"));
            list.add(new Member(27, "Hoàng Nguyên Phi", R.drawable.phi, "Unknown", "phi", "4930", "Phi"));
            list.add(new Member(28, "Đào Minh Phương", R.drawable.phuong, "01659297668", "phuong", "7755", "Phương"));
            list.add(new Member(29, "Trần Thị Như Quỳnh", R.drawable.quynh, "0981515292", "quynh", "4318", "Quỳnh"));
            list.add(new Member(30, "Hoàng Thị Thu Thảo", R.drawable.thao, "0964121901", "thao", "8430", "Thảo"));
            list.add(new Member(31, "Hoàng Thị Thắm", R.drawable.tham, "01634608473", "tham", "4432", "Thắm"));
            list.add(new Member(32, "Bùi Mạnh Thắng", R.drawable.thang, "0961645196", "thang", "1212", "Thắng"));
            list.add(new Member(33, "Đỗ Thị Thu", R.drawable.dothu, "Unknown", "dothu", "3749", "Đ. Thu"));
            list.add(new Member(34, "Trần Thị Thu", R.drawable.tranthu, "0981362939", "tranthu", "2211", "T. Thu"));
            list.add(new Member(35, "Lê Thị Thúy", R.drawable.thuy, "01678601445", "thuy", "0202", "Thúy"));
            list.add(new Member(36, "Lê Thị Huyền Trang", R.drawable.letrang, "01279888311", "letrang", "1389", "L. Trang"));
            list.add(new Member(37, "Đoàn Thị Trang", R.drawable.doantrang, "01662630810", "doantrang", "4643", "Đ. Trang"));
            list.add(new Member(38, "Trần Thị Trinh", R.drawable.trinh, "Unknown", "trinh", "9864", "Trinh"));
            list.add(new Member(39, "Hoàng Văn Trung", R.drawable.trung, "0981859509", "vantrung", "8882", "V. Trung"));
            list.add(new Member(40, "Hoàng Hữu Trung", R.drawable.huutrung, "Unknown", "huutrung", "1317", "H. Trung"));
            list.add(new Member(41, "Vũ Quốc Tuấn", R.drawable.tuan, "01672000762", "tuan", "5314", "Tuấn"));
            list.add(new Member(42, "Đỗ Thị Tươi", R.drawable.tuoi, "0961013184", "tuoi", "7613", "Tươi"));
            list.add(new Member(43, "Bùi Thị Xuân", R.drawable.xuan, "01656738838", "xuan", "0173", "Xuân"));
            list.add(new Member(44, "Hoàng Thị Xuyến", R.drawable.xuyen, "01699077043", "xuyen", "4619", "Xuyến"));
        }
        return list;
    }

    public static class Member {
        public int id;
        public String name;
        public String shortname;
        public int idImage;
        public String phoneNuber;
        public String password;
        public String beautifulName;

        Member(int i, String n, int idm, String phone, String sn, String pass, String b) {
            id = i;
            name = n;
            idImage = idm;
            phoneNuber = phone;
            shortname = sn;
            password = pass;
            beautifulName = b;
        }
    }
}
