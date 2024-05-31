 document.addEventListener('DOMContentLoaded', function () {
        var statusContaSelect = document.getElementById('status');
        var metodoPagamentoBlock = document.getElementById('metodoPagamentoBlock');

        function toggleMetodoPagamento() {
            var status = statusContaSelect.value;
            if (status === 'SOLICITADA' || status === 'FINALIZADA') {
                metodoPagamentoBlock.style.display = '';
            } else {
                metodoPagamentoBlock.style.display = 'none';
            }
        }

        statusContaSelect.addEventListener('change', toggleMetodoPagamento);
        toggleMetodoPagamento();
    });