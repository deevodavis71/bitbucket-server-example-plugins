(function($) {
    // Set up our namespace
    window.MyCompany = window.MyCompany || {};
    MyCompany.TODO = MyCompany.TODO || {};

    // Deal with the nitty-gritty of localStorage
    function storageKey(pullRequestJson) {
        var repo = pullRequestJson.toRef.repository;
        var proj = repo.project;
        return 'mycompany.todo.pullrequest.' + proj.key + '/' + repo.slug + '/' + pullRequestJson.id;
    }
    var storage = window.localStorage ? {
        getTODOs : function(pullRequestJson) {
            var item = localStorage.getItem(storageKey(pullRequestJson));
            try {
                return JSON.parse(item) || [];
            } catch(e) {
                return [];
            }
        },
        putTODOs : function(pullRequestJson, todos) {
            localStorage.setItem(storageKey(pullRequestJson), JSON.stringify(todos));
        }
    } : {
        getTODOs : function() {},
        putTODOs : function() {}
    };

    /**
     * The client-condition function takes in the context
     * before it is transformed by the client-context-provider.
     * If it returns a truthy value, the panel will be displayed.
     */
    function hasAnyTODOs(context) {
        var todos = storage.getTODOs(context['pullRequest']);
        return todos.length;
    }

    /**
     * The client-context-provider function takes in context and transforms
     * it to match the shape our template requires.
     */
    function getTODOStats(context) {
        var todos = storage.getTODOs(context['pullRequest']);
        return {
            count : todos.length
        };
    }

    function addTODO(pullRequestJson, todo) {
        var todos = storage.getTODOs(pullRequestJson);
        todos.push({
            id : new Date().getTime() + ":" + Math.random(),
            text : todo
        });
        storage.putTODOs(pullRequestJson, todos);
    }

    function removeTODO(pullRequestJson, todoId) {
        var todos = storage.getTODOs(pullRequestJson).filter(function(todo) {
            return todo.id != todoId;
        });
        storage.putTODOs(pullRequestJson, todos);
    }


    /* Expose the client-condition function */
    MyCompany.TODO._pullRequestIsOpen = function(context) {
        var pr = context['pullRequest'];
        return pr.state === 'OPEN';
    };

    /* Expose the client-context-provider function */
    MyCompany.TODO.getTODOStats = getTODOStats;

    MyCompany.TODO.addTODO = addTODO;

    MyCompany.TODO.removeTODO = removeTODO;

    function showDialog(todos) {
        var dialog = showDialog._dialog;
        if (!dialog) {
            dialog = showDialog._dialog = new AJS.Dialog()
                .addHeader("TODOs")
                .addPanel("TODOs")
                .addCancel("Close", function() {
                    dialog.hide();
                });
        }

        dialog.getCurrentPanel().body.html(com.mycompany.todo.todoList({ todos: todos }));
        dialog.show().updateHeight();
    }

    function renderTODOsLink() {
        var pr = require('bitbucket/internal/model/page-state').getPullRequest();
        var newStats = MyCompany.TODO.getTODOStats({ pullRequest : pr.toJSON() });
        $('.mycompany-todos-link').replaceWith(com.mycompany.todo.prOverviewPanel(newStats));
    }

    /* use a live event to handle the link being clicked. */
    $(document).on('click', '.mycompany-todos-link', function(e) {
        e.preventDefault();

        var pr = require('bitbucket/internal/model/page-state').getPullRequest();

        showDialog(storage.getTODOs(pr.toJSON()));
    });

    $(document).on('submit', "#create-todo", function(e) {
        e.preventDefault();
        var pr = require('bitbucket/internal/model/page-state').getPullRequest();

        var $input = AJS.$(this).find("input");
        var text = $input.val();
        $input.val('');

        MyCompany.TODO.addTODO(pr.toJSON(), text);
        renderTODOsLink();
    });

    $(document).on('click', '.todo-list .remove', function(e) {
        e.preventDefault();
        var todoId = $(this).closest('li').attr('data-todo-id');

        var prJSON = require('bitbucket/internal/model/page-state').getPullRequest().toJSON();

        MyCompany.TODO.removeTODO(prJSON, todoId);

        showDialog(storage.getTODOs(prJSON));
        renderTODOsLink();
    })
}(AJS.$));