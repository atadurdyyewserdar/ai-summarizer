package com.aissummarizer.jennet.model.response;


/**
 * Immutable metadata about the summarization
 */
public final class SummaryMetadata {
    private final int wordCount;
    private final int imageCount;
    private final int slideCount;
    private final int paragraphCount;
    private final int tableCount;
    private final long processingTimeMs;

    private SummaryMetadata(Builder builder) {
        this.wordCount = Math.max(0, builder.wordCount);
        this.imageCount = Math.max(0, builder.imageCount);
        this.slideCount = Math.max(0, builder.slideCount);
        this.paragraphCount = Math.max(0, builder.paragraphCount);
        this.tableCount = Math.max(0, builder.tableCount);
        this.processingTimeMs = Math.max(0, builder.processingTimeMs);
    }

    // Getters
    public int getWordCount() { return wordCount; }
    public int getImageCount() { return imageCount; }
    public int getSlideCount() { return slideCount; }
    public int getParagraphCount() { return paragraphCount; }
    public int getTableCount() { return tableCount; }
    public long getProcessingTimeMs() { return processingTimeMs; }

    public static final class Builder {
        private int wordCount = 0;
        private int imageCount = 0;
        private int slideCount = 0;
        private int paragraphCount = 0;
        private int tableCount = 0;
        private long processingTimeMs = 0;

        public Builder wordCount(int wordCount) {
            this.wordCount = wordCount;
            return this;
        }

        public Builder imageCount(int imageCount) {
            this.imageCount = imageCount;
            return this;
        }

        public Builder slideCount(int slideCount) {
            this.slideCount = slideCount;
            return this;
        }

        public Builder paragraphCount(int paragraphCount) {
            this.paragraphCount = paragraphCount;
            return this;
        }

        public Builder tableCount(int tableCount) {
            this.tableCount = tableCount;
            return this;
        }

        public Builder processingTimeMs(long processingTimeMs) {
            this.processingTimeMs = processingTimeMs;
            return this;
        }

        public SummaryMetadata build() {
            return new SummaryMetadata(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}