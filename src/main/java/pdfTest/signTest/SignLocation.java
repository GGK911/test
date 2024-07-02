package pdfTest.signTest;

/**
 * 签署坐标实体类
 */
public class SignLocation {

    /**
     * 坐标所在页码
     */
    private int page;

    /**
     * 坐标左下X位置
     */
    private int lbx;

    /**
     * 坐标左下Y位置
     */
    private int lby;

    /**
     * 坐标右上X位置
     */
    private int rtx;

    /**
     * 坐标右上Y位置
     */
    private int rty;

    public SignLocation() {
    }

    private SignLocation(Builder builder) {
        setPage(builder.page);
        setLbx(builder.lbx);
        setLby(builder.lby);
        setRtx(builder.rtx);
        setRty(builder.rty);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLbx() {
        return lbx;
    }

    public void setLbx(int lbx) {
        this.lbx = lbx;
    }

    public int getLby() {
        return lby;
    }

    public void setLby(int lby) {
        this.lby = lby;
    }

    public int getRtx() {
        return rtx;
    }

    public void setRtx(int rtx) {
        this.rtx = rtx;
    }

    public int getRty() {
        return rty;
    }

    public void setRty(int rty) {
        this.rty = rty;
    }

    public void validate() {
        if (page < 1 || lbx < 0 || lby < 0 || rtx < 0 || rty < 0) {
            throw new RuntimeException("invalid coordinate parameter");
        }
    }

    public static final class Builder {
        private int page;
        private int lbx;
        private int lby;
        private int rtx;
        private int rty;

        private Builder() {
        }

        public Builder page(int val) {
            page = val;
            return this;
        }

        public Builder lbx(int val) {
            lbx = val;
            return this;
        }

        public Builder lby(int val) {
            lby = val;
            return this;
        }

        public Builder rtx(int val) {
            rtx = val;
            return this;
        }

        public Builder rty(int val) {
            rty = val;
            return this;
        }

        public SignLocation build() {
            return new SignLocation(this);
        }
    }
}

