// GalleryConfig.java
package com.napominalochka.app.config;

public class GalleryConfig {

    public static final String[][] GALLERY_ITEMS = {
            {"photo", "photo1.jpg", "глупышки", "а мы тут такие веселенькиеееее"},

            {"photo", "photo2.jpg", "масочки", "а тута ты в тикитоке балуешся, пхпхп "},

            {"photo", "photo3.jpg", "пальмаа", "а тут ты в смешнчвоц пальмочкоооойй, пхпзпжрх"},

            {"video", "video1", "АЙАЙАЙАЙАЙАЙЙ", "хулиганачка без шапочки, айайаайай"},

            {"photo", "photo4.jpg", "мымымыммымы", "а туты мыыыы вместееее!"},

            {"photo", "photo5.jpg", "моя красиваяяя", "а тут ты у меня красивенькаяяяя как и всегдааааааа"},

            {"photo", "photo6.jpg", "веночек на льве", "а тут яяяяяя в цвяточках, пхпхпхп!"},

            {"video", "video2", "тьмокиии", "я тебя безумно люблююююююю, милаяяяяяяш!"},

            {"photo", "photo7.jpg", "спатьспатьспать", "а тут моя любименькая девочка спиииттт, ппхрхрх"},

            {"photo", "photo8.jpg", "МЫЫЫЫЫЫЫЫ", "А ТУТА МОЯ ЛЮБИМАЯ НАША ФОТАЧКААААААА"},

            {"photo", "photo9.jpg", "помниш того деда с гитарой", "а тута мы еще и с сонееейй, пхпхпхп"},

            {"photo", "photo10.jpg", "туньтуньтунь", "а тута ты с волосами моими чота делаешшш, пхпхрээ"},

            {"photo", "photo11.jpg", "миимиимимимимиииии", "а тут еще прекраснаяяяяяя наша фоточкаааааааааа!!"},

            {"video", "video3", "полэ", "а помниш мы в поле лежали, рхпх"},

            {"photo", "photo12.jpg", "рисуночееек", "а вот твой прекрасныц и один из моих любимых рисуночков от тебяяяячя, пхпхп"},

            {"photo", "photo13.jpg", "ПЯТКА", "ТВОЯ ПЯТОЧКААА😋😋😈😘🤤🤤😘"}
    };

    public static boolean isVideo(String[] item) {
        return "video".equals(item[0]);
    }

    public static String getFileName(String[] item) {
        return item[1];
    }

    public static String getTitle(String[] item) {
        return item[2];
    }

    public static String getDescription(String[] item) {
        return item[3];
    }
}