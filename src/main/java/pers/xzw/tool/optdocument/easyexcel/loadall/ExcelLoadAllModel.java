package pers.xzw.tool.optdocument.easyexcel.loadall;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

/**
 * 表格实体
 * 目前该实体类只支持读取两行信息
 *
 * 是否能加个配置控制读取多少列？
 */
public class ExcelLoadAllModel extends BaseRowModel {

    /**
     * 第一列的数据
     */
    @ExcelProperty(index = 0)
    private String column1;
    /**
     * 第二列的数据
     */
    @ExcelProperty(index = 1)
    private String column2;

    /**
     * 第三列的数据
     */
    @ExcelProperty(index = 2)
    private String column3;

    /**
     * 第四列的数据
     */
    @ExcelProperty(index = 3)
    private String column4;

    /**
     * 第五列的数据
     */
    @ExcelProperty(index = 4)
    private String column5;

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getColumn4() {
        return column4;
    }

    public void setColumn4(String column4) {
        this.column4 = column4;
    }

    public String getColumn5() {
        return column5;
    }

    public void setColumn5(String column5) {
        this.column5 = column5;
    }

    @Override
    public String toString() {
        return "ExcelLoadAllModel{" +
                "column1='" + column1 + '\'' +
                ", column2='" + column2 + '\'' +
                '}';
    }
}