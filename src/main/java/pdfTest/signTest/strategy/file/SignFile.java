package pdfTest.signTest.strategy.file;


import pdfTest.signTest.strategy.behavior.SignBehavior;

public abstract class SignFile {

    SignBehavior signBehavior;

    public SignFile() {

    }

    //打印签署是ofd还是pdf
    public abstract void disPlay();

    public byte[] sign() {
        byte[] signFile = new byte[0];
        if (signBehavior != null) {
            signFile = signBehavior.sign();
        }
        return signFile;
    }

    public String getSignParam() {
        String signParam = null;
        if (signBehavior != null) {
            signParam = signBehavior.getSignParam();
        }
        return signParam;
    }

}
