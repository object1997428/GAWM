package com.cute.gawm.domain.lookbook_image.entity;

import com.cute.gawm.domain.lookbook.entity.Lookbook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@Entity
@Table(name = "lookbook_image")
@AllArgsConstructor
@NoArgsConstructor
public class LookbookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lookbook_image_id")
    private int lookbookImageId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookbook_id")
    private Lookbook lookbook;
    @Column(name = "image_url")
    private String image;

}
