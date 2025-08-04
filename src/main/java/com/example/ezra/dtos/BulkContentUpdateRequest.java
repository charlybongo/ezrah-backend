package com.example.ezra.dtos;

import com.example.ezra.models.chapterModel.BibleContent;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request payload for bulk updating bible content.
 * Contains a list of content updates and optional IDs to delete.
 */
@Data
@NoArgsConstructor
public class BulkContentUpdateRequest {
    private List<BibleContent> updates;
    private List<Long> deleteIds;
}

