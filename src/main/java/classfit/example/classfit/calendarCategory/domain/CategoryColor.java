package classfit.example.classfit.calendarCategory.domain;

import lombok.Getter;

@Getter
public enum CategoryColor {
    COLOR_1_1("FF71CB"), COLOR_1_2("FF7173"), COLOR_1_3("4867FF"), COLOR_1_4("CF455E"), COLOR_1_5("9F5DD2"),
    COLOR_2_1("19A598"), COLOR_2_2("5DD2A9"), COLOR_2_3("C2CDFF"), COLOR_2_4("FD9326"), COLOR_2_5("FDB456"),
    COLOR_3_1("F6DDE1"), COLOR_3_2("FFEBD0"), COLOR_3_3("D1EEEC"), COLOR_3_4("A59CC7"), COLOR_3_5("E1DDEB");

    private final String hexCode;

    CategoryColor(String hexCode) {
        this.hexCode = hexCode;
    }
}