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
        const commitFilesButtons = context.find(".detail-commits-container").find(".octopus-vcs-show-commit-files");
        if (commitFilesButtons.length > 0) {
            console.debug("Found 'show commit files' button(s), register event listener");
            jQuery('.octopus-vcs-show-commit-files').click(function (event) {
                const target = event.target.attributes["data-key"].nodeValue;
                const targetNode = document.getElementById(target);
                if (targetNode != null) {
                    if (targetNode.classList.contains("hidden")) {
                        console.info('Show files for commit: ' + target);
                        targetNode.classList.remove("hidden");
                    } else {
                        console.info('Hide files for commit: ' + target);
                        targetNode.classList.add("hidden");
                    }
                }
                return false
            })
        }

        const showRepositoryFilesButtons = context.find(".detail-commits-container").find(".octopus-vcs-show-repository-files");
        if (showRepositoryFilesButtons.length > 0) {
            console.debug("Found 'show repository files' button(s), register event listener");
            jQuery('.octopus-vcs-show-repository-files').click(function (event) {
                const target = event.target.attributes["data-key"].nodeValue;
                console.info('Show files for repository: ' + target);
                const targetNodes = document.getElementsByClassName(target);
                for (let targetNode of targetNodes) {
                    if (targetNode.classList.contains("hidden")) {
                        targetNode.classList.remove("hidden");
                    }
                }
                return false
            })
        }

        const hideRepositoryFilesButtons = context.find(".detail-commits-container").find(".octopus-vcs-hide-repository-files");
        if (hideRepositoryFilesButtons.length > 0) {
            console.debug("Found 'hide' repository files button(s), register event listener");
            jQuery('.octopus-vcs-hide-repository-files').click(function (event) {
                const target = event.target.attributes["data-key"].nodeValue;
                console.info('Hide files for repository: ' + target);
                const targetNodes = document.getElementsByClassName(target);
                for (let targetNode of targetNodes) {
                    if (!targetNode.classList.contains("hidden")) {
                        targetNode.classList.add("hidden")
                    }
                }
                return false
            })
        }
    });
});
