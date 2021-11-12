package su.demands.asciiTable;

import java.util.function.Function;

public class ColumnData<T> extends Column {
    private final Function<T, String> getter;

    ColumnData(Column column, Function<T, String> getter) {
        super(column.getHeader(), column.getFooter(), column.getHeaderAlign(), column.getDataAlign(),
                column.getFooterAlign(), column.getMaxColumnWidth());
        this.getter = getter;
    }

    
    /** 
     * @param object
     * @return String
     */
    public String getCellValue(T object) {
        return getter.apply(object);
    }
}