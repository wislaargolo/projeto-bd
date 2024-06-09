// mascara de valor

$(document).ready(function(){
    var valorInicial = $('#valor').val();
    if (valorInicial) {
        valorInicial = parseFloat(valorInicial).toFixed(2).replace('.', ',');
        $('#valor').val(valorInicial);
    }

    $('#valor').mask('#.##0,00', {reverse: true});

    $('form').submit(function() {
        var valor = $('#valor').val();
        valor = valor.replace(/\./g, '').replace(',', '.');
        $('#valor').val(valor);
    });
});

// hamburguer

const hamBurger = document.querySelector(".toggle-btn");

  hamBurger.addEventListener("click", function () {
    document.querySelector("#sidebar").classList.toggle("expand");
  });


// verifica opcao ativa do sidebar
document.addEventListener("DOMContentLoaded", function() {
  const sidebarLinks = document.querySelectorAll('.sidebar-link');
  const currentPath = window.location.pathname;

  function isActive(linkPath) {
    return currentPath.startsWith(linkPath);
  }

  sidebarLinks.forEach(link => {
    const linkPath = link.getAttribute('href');

    if (isActive(linkPath)) {
      link.classList.add('active');
    }
  });

  const funcionariosSublinks = document.querySelectorAll('#auth a.sidebar-link');
  let funcionariosActive = Array.from(funcionariosSublinks).some(sublink => {
    return isActive(sublink.getAttribute('href'));
  });

  if (funcionariosActive) {
    const funcionariosMainLink = document.querySelector('a.has-dropdown[data-bs-target="#auth"]');
    funcionariosMainLink.classList.add('active');
  }
});


// fechar modal
$(document).ready(function(){

    $(".close, .btn-default").click(function(){
        $("#myModal").modal('hide');
    });
});


