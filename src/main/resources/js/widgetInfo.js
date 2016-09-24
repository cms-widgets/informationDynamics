/**
 * Created by lhx on 2016/8/11.
 */
CMSWidgets.initWidget({
// 编辑器相关
    editor: {
        saveComponent: function (onFailed) {
            // this.properties.serial = $(".category").val();
            // this.properties.title = $('select.category option:selected').text() + '';
            this.properties.size = $(".size").val();
            if (this.properties.serial == null || this.properties.serial == '' || this.properties.size == ''
                || this.properties.size == '0') {
                onFailed("数据源serial和展示条数不能为空和0");
                return;
            }

        }
    }
});
