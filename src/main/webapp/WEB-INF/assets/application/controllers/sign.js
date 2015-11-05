ASMX.Binding.new('controller:sign', function($form) {
    var $formUi = $form.find('ui.form');
    var $submitButton = $form.find('.submit.button');
    var $inputs = $form.find('[data-ajax]');

    function collectFormData() {
        var data = {};
        $inputs.each(function() {
            var $input = $(this);
            var name = $input.attr('name');
            if (name !== undefined) {
                data[name] = $input.val();
            }
        });
        return data;
    }

    function signIn() {
        // If a field is already disabled then it wont be enabled when the request finishes
        var $inputsToLock = $inputs.filter(':not(:disabled)');
        var requestData = collectFormData();

        $.ajax(declaration('address:sign'), {
            type: 'POST',
            dataType: 'json',
            data: requestData,
            beforeSend: function() {
                $formUi.addClass('disabled');
                $submitButton.addClass('loading');
                $inputsToLock.prop('disabled', true);
            },
            complete: function() {
                $formUi.removeClass('disabled');
                $submitButton.removeClass('loading');
                $inputsToLock.prop('disabled', false);
            },
            success: function(data) {
                ASMX.Ajax.done(data, function(data) {
                    switch (data.statusCode) {
                        case declaration('RESPONSE:SUCCESS'):
                            ASMX.Storage.set('TIMESTAMP_PATTERN', data.timestampPattern);
                            location.href = declaration('address:notes');
                            break;

                        case declaration('RESPONSE:UNAUTHORISED'):
                            ASMX.Messages.show({
                                title: tr('sign.unauthorized.title'),
                                message: tr('sign.unauthorized'),
                                classes: declaration('MESSAGE_CLASS:ERROR'),
                                id: 'unauthorized'
                            });
                            $inputs.filter('[type="password"]').val("");
                            return false;
                    }
                });
            }
        });
    }

    $form.form({
        fields: {
            username: {
                identifier: 'username',
                rules: [{
                    type: 'empty',
                    prompt: tr('error.form.field_required')
                }]
            },
            password: {
                identifier: 'password',
                rules: [{
                    type: 'empty',
                    prompt: tr('error.form.field_required')
                }]
            }
        },
        inline: true,
        onSuccess: function() {
            signIn();
            return false;
        }
    });
});
