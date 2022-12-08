package pers.xzw.tool.optdocument;

/**
 * 读取excel
 */
public interface ExcelReader {

    /**
     * 读取指定目录的excel文档，并进行业务处理。不进行任何数据的返回
     */
    void read(String path, BusinessHandler businessHandler);
}
