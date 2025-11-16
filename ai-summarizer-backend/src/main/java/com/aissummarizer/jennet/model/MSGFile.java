package com.aissummarizer.jennet.model;

import com.aissummarizer.jennet.constant.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@AllArgsConstructor
@Data
public class MSGFile {
    File file;
    FileType fileType;
}
