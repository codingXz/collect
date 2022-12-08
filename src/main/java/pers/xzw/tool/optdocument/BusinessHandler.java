package pers.xzw.tool.optdocument;

/**
 * 业务处理对象基类
 */
@FunctionalInterface
public interface BusinessHandler {
    Object call(Object object);
}
