import { readFileSync } from 'node:fs';
import assert from 'node:assert/strict';
import { initializeTestEnvironment, assertFails, assertSucceeds } from '@firebase/rules-unit-testing';
import { doc, setDoc, getDoc, collection, onSnapshot } from 'firebase/firestore';
import { ref, uploadBytes, getBytes } from 'firebase/storage';

const env = await initializeTestEnvironment({
  projectId: 'demo-pmdm',
  firestore: { host: '127.0.0.1', port: 8085, rules: readFileSync('firestore.rules', 'utf8') },
  storage: { host: '127.0.0.1', port: 9199, rules: readFileSync('storage.rules', 'utf8') },
});
let passed = 0;
try {
  await env.clearFirestore(); await env.clearStorage();
  const owner = env.authenticatedContext('ana');
  const other = env.authenticatedContext('bea');
  const anon = env.unauthenticatedContext();
  await assertFails(getDoc(doc(anon.firestore(), 'usuarios/ana/notas/n1'))); passed++;
  await assertSucceeds(setDoc(doc(owner.firestore(), 'usuarios/ana/notas/n1'), { titulo: 'Primera nota' }));
  assert.equal((await getDoc(doc(owner.firestore(), 'usuarios/ana/notas/n1'))).data().titulo, 'Primera nota'); passed++;
  await assertFails(getDoc(doc(other.firestore(), 'usuarios/ana/notas/n1'))); passed++;
  await assertFails(setDoc(doc(other.firestore(), 'usuarios/ana/notas/n2'), { titulo: 'Intrusión' })); passed++;
  await assertFails(setDoc(doc(owner.firestore(), 'usuarios/ana/notas/n2'), { titulo: 'ab' })); passed++;
  await assertFails(setDoc(doc(owner.firestore(), 'usuarios/ana/notas/n2'), { titulo: '   ' })); passed++;
  await assertFails(setDoc(doc(owner.firestore(), 'usuarios/ana/notas/n2'), { titulo: 'Válida', extra: true })); passed++;
  await new Promise((resolve, reject) => {
    const timer = setTimeout(() => { stop(); reject(new Error('No llegó actualización')); }, 5000);
    const stop = onSnapshot(collection(owner.firestore(), 'usuarios/ana/notas'), snap => {
      if (snap.docs.some(d => d.id === 'n3')) { clearTimeout(timer); stop(); resolve(); }
    }, error => { clearTimeout(timer); reject(error); });
    setDoc(doc(owner.firestore(), 'usuarios/ana/notas/n3'), { titulo: 'Actualización' }).catch(reject);
  }); passed++;
  const bytes = new Uint8Array([137,80,78,71,13,10,26,10]);
  await assertSucceeds(uploadBytes(ref(owner.storage(), 'usuarios/ana/imagen'), bytes, { contentType: 'image/png' }));
  assert.deepEqual(new Uint8Array(await getBytes(ref(owner.storage(), 'usuarios/ana/imagen'))), bytes); passed++;
  await assertFails(getBytes(ref(other.storage(), 'usuarios/ana/imagen'))); passed++;
  await assertFails(uploadBytes(ref(owner.storage(), 'usuarios/ana/imagen'), bytes, { contentType: 'text/plain' })); passed++;
  await assertFails(uploadBytes(ref(owner.storage(), 'usuarios/ana/imagen'), new Uint8Array(1024*1024+1), { contentType: 'image/png' })); passed++;
  const email = `laboratorio-${Date.now()}@example.test`;
  const signup = await fetch('http://127.0.0.1:9099/identitytoolkit.googleapis.com/v1/accounts:signUp?key=demo-api-key', {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password: 'Solo-Emulador-84', returnSecureToken: true }),
  });
  assert.equal(signup.status, 200);
  const signin = await fetch('http://127.0.0.1:9099/identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=demo-api-key', {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password: 'Solo-Emulador-84', returnSecureToken: true }),
  });
  assert.equal(signin.status, 200); passed++;
  console.log(`OK ${passed} comprobaciones reales de Emulator Suite: reglas, tiempo real, archivos y cuenta local.`);
} finally { await env.cleanup(); }
