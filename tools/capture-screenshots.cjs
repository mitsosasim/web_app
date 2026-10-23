// Run against the local synthetic demo. Requires Playwright and its Chromium browser.
const { chromium } = require('playwright');
const path = require('node:path');
const fs = require('node:fs');
const assert = require('node:assert/strict');
const base = process.env.PETCARE_BASE_URL || 'http://127.0.0.1:8080/pet-care/';
if (!['127.0.0.1','localhost'].includes(new URL(base).hostname)) throw new Error('Use the local synthetic demo.');
const output = path.resolve(__dirname, '../docs/assets');
fs.mkdirSync(output, {recursive:true});
(async () => {
  const browser = await chromium.launch({headless:true, args:['--no-sandbox']});
  const errors = [];
  const external = [];
  async function open() {
    const context = await browser.newContext({viewport:{width:1360,height:900},deviceScaleFactor:1});
    const page = await context.newPage();
    page.on('pageerror', e => errors.push(e.message));
    await page.route('**/*', route => {
      const url = new URL(route.request().url());
      if (url.hostname !== new URL(base).hostname) { external.push(url.hostname); return route.abort(); }
      return route.continue();
    });
    await page.goto(base, {waitUntil:'networkidle'});
    return {context,page};
  }
  try {
    {
      const {context,page} = await open();
      await page.getByRole('button',{name:'Search',exact:true}).click();
      await page.waitForFunction(() => document.querySelectorAll('#previewKeepers tr').length === 4);
      await page.screenshot({path:path.join(output,'keeper-directory.png'),clip:{x:0,y:0,width:1360,height:520}});
      await context.close();
    }
    for (const [user,pass,name,expected] of [
      ['admin','admin12*','admin-dashboard','profits of petkeepers'],
      ['demo_keeper','Demo123!','keeper-booking','Accept']
    ]) {
      const {context,page} = await open();
      await page.getByRole('button',{name:'Login',exact:true}).click();
      await page.locator('#loginUsername').fill(user);
      await page.locator('#loginPassword').fill(pass);
      await page.locator('#submitLogin').click();
      await page.getByText(expected,{exact:true}).waitFor();
      if (user === 'demo_keeper') {
        const table = page.locator('#displayLoginCredentials table').filter({hasText:'booking_id'}).last();
        await table.scrollIntoViewIfNeeded();
        const box = await table.boundingBox();
        const top = Math.max(0, box.y - 12);
        await page.screenshot({path:path.join(output,name+'.png'),clip:{x:0,y:top,width:1360,height:Math.min(box.height+100,900-top)}});
      } else {
        await page.screenshot({path:path.join(output,name+'.png'),clip:{x:0,y:0,width:1360,height:810}});
      }
      if (user === 'demo_keeper') {
        await page.getByRole('button',{name:'Accept',exact:true}).click();
        const response = await page.request.post(new URL('getBookingToKeeper',base).href,{form:{user:JSON.stringify({id:'1'})}});
        assert.equal((await response.json()).str,undefined,'Accepted booking must leave the pending list');
      }
      await context.close();
    }
    assert.deepEqual(errors,[],'Browser errors');
    assert.deepEqual(external,[],'Unexpected external requests');
    console.log(JSON.stringify({screenshots:3,browserErrors:errors,externalRequests:external,bookingAcceptance:'verified'}));
  } finally { await browser.close(); }
})().catch(error => {console.error(error);process.exit(1);});
