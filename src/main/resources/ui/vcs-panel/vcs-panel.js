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

AJS.$(function () {
    JIRA.bind(JIRA.Events.NEW_CONTENT_ADDED, function (e, context, reason) {
        const showFilesButtons = context.find(".detail-commits-container").find(".octopus-vcs-show-files")
        if (showFilesButtons.length > 0) {
            console.debug('Found show files button, register event listener');
            jQuery('.octopus-vcs-show-files').click(function (event) {
                let target = event.target.attributes["data-key"].nodeValue;
                console.info('Show files for commit: ' + target);
                const targetNode = document.getElementById(target)
                if (targetNode != null) {
                    console.debug(`Found node: ${targetNode.nodeName}`)
                    if (targetNode.classList.contains("hidden")) {
                        console.debug(`Node ${targetNode.nodeName} is hidden, show`)
                        targetNode.classList.remove("hidden")
                    } else {
                        console.debug(`Node ${targetNode.nodeName} is visible, hide`)
                        targetNode.classList.add("hidden")
                    }
                }
                return false
            });
        }


    })
});