jQuery(function () {
    new AJS.FormPopup({
        id: "devstatus-commit-detail-dialog",
        trigger: "#view-commits",
        width: 1000,
    });
});

jQuery(function () {
    new AJS.FormPopup({
        id: "devstatus-branch-detail-dialog",
        trigger: "#view-branches",
        width: 1000
    });
});

jQuery(function () {
    new AJS.FormPopup({
        id: "devstatus-pullrequest-detail-dialog",
        trigger: "#view-pull-requests",
        width: 1000
    });
});
