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
    const regex = new RegExp(`^${linkPath}(/|$)`);
    return regex.test(currentPath);
  }

  sidebarLinks.forEach(link => {
    const linkPath = link.getAttribute('href');
    if (isActive(linkPath) && (currentPath === linkPath || currentPath === linkPath + '/')) {
      link.classList.add('active');
    }
  });

  function checkSublinksActive(sublinksSelector, dropdownTarget) {
    const sublinks = document.querySelectorAll(sublinksSelector);
    const isActiveSublink = Array.from(sublinks).some(sublink => {
      return isActive(sublink.getAttribute('href'));
    });

    if (isActiveSublink) {
      const mainLink = document.querySelector(`a.has-dropdown[data-bs-target="${dropdownTarget}"]`);
      mainLink.classList.add('active');
    }
  }

  checkSublinksActive('#auth a.sidebar-link', '#auth');
  checkSublinksActive('#orders a.sidebar-link', '#orders');
});


// fechar modal
$(document).ready(function(){

    $(".close, .btn-default").click(function(){
        $("#myModal").modal('hide');
    });
});


//mascara telefone
 $(document).ready(function(){
           $('#telefone').mask('(00) 00000-0000');
});

// tela de pedidos para atendentes
 function toggleProducts(pedidoId) {
     var productList = document.getElementById('products-' + pedidoId);
     console.log('Toggle function called for: ', pedidoId); // Adicione isso para confirmar que a função é chamada
     if (productList) {
         console.log('Found element: ', productList); // Confirma que o elemento foi encontrado
         productList.style.display = (productList.style.display === 'none' || productList.style.display === '') ? 'table-row' : 'none';
     } else {
         console.error('Elemento não encontrado: products-' + pedidoId);
     }
 }

 $(window).on('load', function() {
     $('.btn-visualizar').on('click', function() {
         var pedidoId = $(this).data('pedido-id');
         toggleProducts(pedidoId);
     });
 });




