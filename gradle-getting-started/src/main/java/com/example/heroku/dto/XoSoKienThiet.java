package com.example.heroku.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XoSoKienThiet {
    private String maKQ;
    private String maCongTy;
    private String NgayXoSo;
    private String GiaiDacBiet;
    private String GiaiNhat;
    private String GiaiNhi;
    private String GiaiBa;
    private String GiaiTu;
    private String GiaiNam;
    private String GiaiSau;
    private String GiaiBay;
    private String GiaiTam;

    public XoSoKienThiet(String resultSplitByColon) {

    }
}
