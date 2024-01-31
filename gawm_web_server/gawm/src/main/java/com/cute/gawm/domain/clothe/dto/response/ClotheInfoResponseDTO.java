package com.cute.gawm.domain.clothe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ClotheInfoResponseDTO {
    private int clotheId;
    private int userId;
    private int orderNum;
    private String clotheImg;
    private String mCategory;
    private String sCategory;
    private String brand;
    private String name;
    private List<String> colors;
    private List<String> materials;
    private List<String> patterns;

}
