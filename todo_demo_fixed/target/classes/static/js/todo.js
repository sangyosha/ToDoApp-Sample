document.addEventListener('DOMContentLoaded', () => {

  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

  // --- フィルター ---
  let currentFilter = 'all';
  const filterMap = { filterAll: 'all', filterIncomplete: 'incomplete', filterComplete: 'complete' };

  Object.keys(filterMap).forEach(id => {
    document.getElementById(id).addEventListener('click', () => {
      currentFilter = filterMap[id];
      renderFilter();
    });
  });

  function renderFilter() {
    let countAll=0, countIncomplete=0, countComplete=0;
    document.querySelectorAll('#todoList li').forEach(li => {
      const done = li.getAttribute('data-done') === 'true';
      countAll++;
      if(done) countComplete++; else countIncomplete++;

      li.classList.remove('d-none');
      if(currentFilter==='incomplete' && done) li.classList.add('d-none');
      if(currentFilter==='complete' && !done) li.classList.add('d-none');
    });
    document.getElementById('countAll').innerText = countAll;
    document.getElementById('countIncomplete').innerText = countIncomplete;
    document.getElementById('countComplete').innerText = countComplete;

    Object.keys(filterMap).forEach(id => document.getElementById(id).classList.remove('active'));
    document.getElementById(Object.keys(filterMap).find(id => filterMap[id]===currentFilter)).classList.add('active');
  }

  // --- チェックボックスで即更新 ---
  document.querySelectorAll('.todo-checkbox').forEach(cb => {
    cb.addEventListener('change', () => {
      fetch('/todos/toggle/' + cb.value, {
        method: 'POST',
        headers: { [csrfHeader]: csrfToken }
      }).then(()=>location.reload());
    });
  });

  // --- 編集 ---
  document.querySelectorAll('.editBtn').forEach(btn=>{
    btn.addEventListener('click', e=>{
      const li = e.target.closest('li');
      const id = li.getAttribute('data-id');
      const currentText = li.querySelector('span').innerText;
      const newText = prompt('編集内容を入力してください', currentText);
      if(newText && newText.trim()!==''){
        fetch('/todos/update/'+id,{
          method:'POST',
          headers:{'Content-Type':'application/json',[csrfHeader]:csrfToken},
          body:JSON.stringify({task:newText.trim()})
        }).then(()=>location.reload());
      }
    });
  });

  // --- 単体削除 ---
  document.querySelectorAll('.deleteBtn').forEach(btn=>{
    btn.addEventListener('click', e=>{
      const li = e.target.closest('li');
      const id = li.getAttribute('data-id');
      if(confirm('削除しますか？')){
        fetch('/todos/delete/'+id,{
          method:'POST',
          headers:{[csrfHeader]:csrfToken}
        }).then(()=>location.reload());
      }
    });
  });

  // --- すべて完了/未完了切替 ---
  document.getElementById('toggleAllBtn').addEventListener('click', () => {
    const allCheckboxes = Array.from(document.querySelectorAll('.todo-checkbox'));
    if(allCheckboxes.length === 0) return;

    const anyIncomplete = allCheckboxes.some(cb => !cb.checked);
    const targetDone = anyIncomplete;

    const ids = allCheckboxes.map(cb => parseInt(cb.value));

    fetch('/todos/toggleAll', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
      },
      body: JSON.stringify({ ids: ids, done: targetDone })
    }).then(res => res.json())
      .then(data => {
        if(data.success) location.reload();
        else alert('切替に失敗しました');
      });
  });

  renderFilter();
});
