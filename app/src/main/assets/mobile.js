(function(){
  function init(){
    var header=document.querySelector('header .wrap');
    if(header && !document.querySelector('.rockford-mobile-header')){
      var original=header.innerHTML;
      header.innerHTML='<div class="rockford-mobile-header"><div class="rockford-mobile-title">'+original+'</div><button class="rockford-hamburger" aria-label="Menu">☰</button></div>';
      var wrap=document.querySelector('main.wrap');
      var tabs=document.querySelector('.tabs');
      if(wrap && tabs && !document.querySelector('.rockford-mobile-menu')){
        var menu=document.createElement('div'); menu.className='rockford-mobile-menu';
        var items=[['hala','▦  Hala Šumperk'],['byty','⌂  Byty'],['platbykontrola','▣  Kontrola plateb'],['mesice','▦  Měsíc'],['platby','＋  Ruční platby'],['dluhy','⚠  Dluhy']];
        items.forEach(function(it){var b=document.createElement('button');b.textContent=it[1];b.onclick=function(){var target=document.getElementById(it[0]);document.querySelectorAll('.panel').forEach(function(x){x.classList.remove('active')});if(target)target.classList.add('active');menu.classList.remove('open');window.scrollTo({top:0,behavior:'smooth'});};menu.appendChild(b);});
        tabs.parentNode.insertBefore(menu,tabs.nextSibling);
      }
      var hb=document.querySelector('.rockford-hamburger'); if(hb)hb.onclick=function(){var m=document.querySelector('.rockford-mobile-menu');if(m)m.classList.toggle('open')};
    }
  }
  if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',init);else init();
})();
