package com.cute.gawm.domain.following.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "following")
public class Following {

    @Id
    @Field(value = "user_id", targetType = FieldType.OBJECT_ID)
    private int userId;

    @Field(value = "following_id_list")
    private List<Integer> followingList;

    public void update(List<Integer> followingList){
        this.followingList=followingList;
    }
}