package DownloadFile;

public enum FileType {
    //policy_application_form
    eApp,
    //proposal
    bi,
    //casedata
    casedata;

    String fileType;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        if (fileType == eApp) {
            this.fileType = "Proposal";
        } else if (fileType == bi){
            this.fileType = "PI";
        } else {
            this.fileType = fileType.toString();
        }
    }
}
