package com.aissummarizer.jennet.model;

import com.aissummarizer.jennet.constant.FileType;
import lombok.AllArgsConstructor;

import java.io.File;

@AllArgsConstructor
public class MSGFile {
    File file;
    FileType fileType;
}
