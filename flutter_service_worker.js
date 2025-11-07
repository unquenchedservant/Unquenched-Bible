'use strict';
const MANIFEST = 'flutter-app-manifest';
const TEMP = 'flutter-temp-cache';
const CACHE_NAME = 'flutter-app-cache';

const RESOURCES = {"canvaskit/canvaskit.js": "140ccb7d34d0a55065fbd422b843add6",
"canvaskit/canvaskit.js.symbols": "58832fbed59e00d2190aa295c4d70360",
"canvaskit/canvaskit.wasm": "07b9f5853202304d3b0749d9306573cc",
"canvaskit/chromium/canvaskit.js": "5e27aae346eee469027c80af0751d53d",
"canvaskit/chromium/canvaskit.js.symbols": "193deaca1a1424049326d4a91ad1d88d",
"canvaskit/chromium/canvaskit.wasm": "24c77e750a7fa6d474198905249ff506",
"canvaskit/skwasm.js": "1ef3ea3a0fec4569e5d531da25f34095",
"canvaskit/skwasm.js.symbols": "0088242d10d7e7d6d2649d1fe1bda7c1",
"canvaskit/skwasm.wasm": "264db41426307cfc7fa44b95a7772109",
"canvaskit/skwasm_heavy.js": "413f5b2b2d9345f37de148e2544f584f",
"canvaskit/skwasm_heavy.js.symbols": "3c01ec03b5de6d62c34e17014d1decd3",
"canvaskit/skwasm_heavy.wasm": "8034ad26ba2485dab2fd49bdd786837b",
"flutter.js": "888483df48293866f9f41d3d9274a779",
"flutter_bootstrap.js": "a7cb266a87e129d9035cf07ebd72108e",
"index.html": "82463a92110aa0492f0ad8453d02ae32",
"/": "82463a92110aa0492f0ad8453d02ae32",
"main.dart.js": "8bb24bfec523adf8a6b599b563703453",
"version.json": "54cec5660798dd48a756b9c5a0903e6d",
"assets/packages/material_design_icons_flutter/lib/fonts/materialdesignicons-webfont.ttf": "d10ac4ee5ebe8c8fff90505150ba2a76",
"assets/packages/cupertino_icons/assets/CupertinoIcons.ttf": "33b7d9392238c04c131b6ce224e13711",
"assets/packages/font_awesome_flutter/lib/fonts/Font-Awesome-7-Free-Regular-400.otf": "b2703f18eee8303425a5342dba6958db",
"assets/packages/font_awesome_flutter/lib/fonts/Font-Awesome-7-Free-Solid-900.otf": "5b8d20acec3e57711717f61417c1be44",
"assets/packages/font_awesome_flutter/lib/fonts/Font-Awesome-7-Brands-Regular-400.otf": "a55215c841681826aa0d1e3f6c5d507f",
"assets/fonts/MaterialIcons-Regular.otf": "4557d39be99a940f6638099224853fac",
"assets/shaders/ink_sparkle.frag": "ecc85a2e95f5e9f53123dcaf8cb9b6ce",
"assets/AssetManifest.json": "b5f384b6750d4240ced3851ced70ad81",
"assets/AssetManifest.bin": "339233b0bab200f416dcdf887081aaa6",
"assets/AssetManifest.bin.json": "30ec64f31f22ec4bbe4757880cb6ec82",
"assets/FontManifest.json": "b7adc8fda17ad1060874b0b604bfa735",
"assets/NOTICES": "ee37be2ab0d024396e5cbe83134e6370",
"icons/Icon-192.png": "ac9a721a12bbc803b44f645561ecb1e1",
"icons/Icon-512.png": "96e752610906ba2a93c65f8abe1645f1",
"icons/Icon-maskable-192.png": "c457ef57daa1d16f64b27b786ec2ea3c",
"icons/Icon-maskable-512.png": "301a7604d45b3e739efc881eb04896ea",
"favicon.png": "5dcef449791fa27946b3d35ad8803796",
"manifest.json": "5428c8fd66e13508387cbdfe38f86fa3",
"privacy_policy.html": "94ed0e95f1511b6d642016d257ccb58d",
"404.html": "d86e02b4b8b59fc21162c245a24b5add",
".git/hooks/applypatch-msg.sample": "ce562e08d8098926a3862fc6e7905199",
".git/hooks/commit-msg.sample": "579a3c1e12a1e74a98169175fb913012",
".git/hooks/fsmonitor-watchman.sample": "a0b2633a2c8e97501610bd3f73da66fc",
".git/hooks/post-update.sample": "2b7ea5cee3c49ff53d41e00785eb974c",
".git/hooks/pre-applypatch.sample": "054f9ffb8bfe04a599751cc757226dda",
".git/hooks/pre-commit.sample": "5029bfab85b1c39281aa9697379ea444",
".git/hooks/pre-merge-commit.sample": "39cb268e2a85d436b9eb6f47614c3cbc",
".git/hooks/pre-push.sample": "2c642152299a94e05ea26eae11993b13",
".git/hooks/pre-rebase.sample": "56e45f2bcbc8226d2b4200f7c46371bf",
".git/hooks/pre-receive.sample": "2ad18ec82c20af7b5926ed9cea6aeedd",
".git/hooks/prepare-commit-msg.sample": "2b5c047bdb474555e1787db32b2d2fc5",
".git/hooks/push-to-checkout.sample": "c7ab00c7784efeadad3ae9b228d4b4db",
".git/hooks/sendemail-validate.sample": "4d67df3a8d5c98cb8565c07e42be0b04",
".git/hooks/update.sample": "647ae13c682f7827c22f5fc08a03674e",
".git/info/exclude": "036208b4a1ab4a235d75c181e685e5a3",
".git/description": "a0a7c3fff21f2aea3cfa1d0316dd816c",
".git/refs/heads/main": "0b210e4b7d93613a2d2a33804b567c47",
".git/refs/remotes/origin/main": "0b210e4b7d93613a2d2a33804b567c47",
".git/refs/remotes/origin/HEAD": "98b16e0b650190870f1b40bc8f4aec4e",
".git/objects/76/e0cfde241f59ba59948af10fb5829e699c2d1b": "b785c82892434320d8f93d66c37cc886",
".git/objects/04/daf17ef20d35c8fcf23e943e18d85700169619": "76fc622e316c8c92ae054886954d0ff8",
".git/objects/36/0211c1a01783492308b17b676e49b594dc48d9": "5480560e84dbc5baa2e186bf8e947622",
".git/objects/49/16a1c0fd386ca8e1d79e6b72c762db67115b7a": "dedf4c3923ae80d039835805be31d2b8",
".git/objects/9a/362c1a4a062fc7da4239c4d921e474ca64bef5": "643f57d1a268a7b53c4ccfc8cb189512",
".git/objects/b0/5402a4222842b341d200f754de66e51434a833": "7974d934df9700ca5fc20f7fd143ea16",
".git/objects/b0/0c684d3ef14be87f0badd2eecc88babc70fea0": "62b179c5e55028c8088598f00ea6c781",
".git/objects/b0/a9fb7c4f7d7eae2bba2b82b8d7183dcc272de6": "a4d0ae878802a6e3c9e36aaa0a9b1eca",
".git/objects/c2/08b672bf6dd68015685f6c7a989da090c6e7e7": "a22c27d253def52e523c9e86dc856238",
".git/objects/6a/d24ec619b50ef72445007ca436e7d73fff216d": "26cc75b0749fd7c99a98b2aa34b913b7",
".git/objects/e9/94225c71c957162e2dcc06abe8295e482f93a2": "70d7e891b9a9a44b4a65cd9990250a20",
".git/objects/e9/4fcb3a9590f58eec55f6ee24c62a846abbe337": "9b6ffd7090d6e4478ea3619b14b812bc",
".git/objects/d4/3532a2348cc9c26053ddb5802f0e5d4b8abc05": "9e0a7dce91540443aeee8c8cd1dcd7df",
".git/objects/7a/6c1911dddaea52e2dbffc15e45e428ec9a9915": "9be869e0f2a532b8d9478d6e87c5651f",
".git/objects/98/0d49437042d93ffa850a60d02cef584a35a85c": "65d18a9837aeb392a90de68393cdffdf",
".git/objects/98/2f4b15250237f2030cba81047b9a0b9bcef5cc": "f7a0974694e8dfaa59af89125807bb8a",
".git/objects/98/64feaf6f3224a594ce292045a6a710fc388da1": "16ea57dcbb1d63f314feafb2a92d0ffa",
".git/objects/4d/bf9da7bcce5387354fe394985b98ebae39df43": "0e814c95743da9ea171bf1decf280426",
".git/objects/b6/b8806f5f9d33389d53c2868e6ea1aca7445229": "fb4469623771e09349b55fdfac63c384",
".git/objects/9b/3ef5f169177a64f91eafe11e52b58c60db3df2": "3278c0abd1b752fe3fa5b8db850d7d2e",
".git/objects/29/f22f56f0c9903bf90b2a78ef505b36d89a9725": "3a89fbf457bc2e25769ea12fc25cbda0",
".git/objects/ca/3bba02c77c467ef18cffe2d4c857e003ad6d5d": "45334b0198ddc18d9c58f1dd9acbd2d7",
".git/objects/c4/016f7d68c0d70816a0c784867168ffa8f419e1": "0fc0a5b3bd7ab6741d66a114a90cf074",
".git/objects/c4/744a95ca24e385a77592b356a3e45ec1268f6d": "bd4725b06f496af9af5b5a515e290a34",
".git/objects/c4/58d8886b6e140cc9137717efc7648afed56465": "7ab8f465d256535f35f739937155dfef",
".git/objects/ed/b55d4deb8363b6afa65df71d1f9fd8c7787f22": "6d7d63ee29850883161729da29f76056",
".git/objects/20/3a3ff5cc524ede7e585dff54454bd63a1b0f36": "33121d4fda2c7d3071b038d9f116e342",
".git/objects/9e/3b4630b3b8461ff43c272714e00bb47942263e": "d51750c088c514598acbb1e6dec6ee4e",
".git/objects/4f/fbe6ec4693664cb4ff395edf3d949bd4607391": "c51611bb14cd84d8f9410068ad6728af",
".git/objects/4f/04a70d23507cec14ae27ddbf6d7174941ef1ba": "ae409db390ebdaca3d02b40ee198f4d4",
".git/objects/4f/40dabe4430b4d7288c6ae92d8fc3e17cf8e410": "99eca29a61b4d0fb465bf981cd75ff25",
".git/objects/8a/aa46ac1ae21512746f852a42ba87e4165dfdd1": "af85c278711e6c817f328f12fc54f4bc",
".git/objects/fe/3b987e61ed346808d9aa023ce3073530ad7426": "0e99633a6098bc621e976bd2117f2be1",
".git/objects/b7/49bfef07473333cf1dd31e9eed89862a5d52aa": "03dc7c3b440d9d36b3e1dc2ab06f364c",
".git/objects/88/cfd48dff1169879ba46840804b412fe02fefd6": "38c08fdc0dbc026fb372f8f06543db01",
".git/objects/eb/9b4d76e525556d5d89141648c724331630325d": "36264bdfab0a32a2aefdc668e36d971d",
".git/objects/d6/9c56691fbdb0b7efa65097c7cc1edac12a6d3e": "3a663110118a15ba1de956e30344cd34",
".git/objects/c9/298c490de6a76dd48c20023cb71abde4884baf": "5b566ab944445887844119347f943c37",
".git/objects/2e/85ee5a35e662de34809227d926c9b33e885805": "81bfab96aa58732c2268b42ae364f662",
".git/objects/bd/4050f1261e4f80269c181621a75a05922535ab": "833cc3af967ce726e13f6ecb757d1938",
".git/objects/c3/e2ee34daf1b1a7fceeefb8ea4d16547fa05ad2": "ef5817c03979f47b24f579bdbd10b159",
".git/objects/4c/8662e21acac47809edd54423ae7d930f5084ea": "8066fea301b17c5f30a1f9f254f4d5c0",
".git/objects/4c/8337d7cded0cdbc46a0c158c2df8d75ee93015": "2262e120195666bf4ca1c383ecf63765",
".git/objects/6b/9862a1351012dc0f337c9ee5067ed3dbfbb439": "85896cd5fba127825eb58df13dfac82b",
".git/objects/f5/72b90ef57ee79b82dd846c6871359a7cb10404": "c86893109644c17e9714c1db6ccfdcce",
".git/objects/79/38f52df34b597404d0034ae3074064bae5de41": "79f0b48b85bd72bec5b9b27fc604f106",
".git/objects/99/b3a5b6ec66be4cec842529a05d39c4c95d2cfc": "4c3aa1943eec105edd7f1c3d028b05a0",
".git/objects/99/f9cc4ab8a4eae3c55c6f4523b2a0a7c8ee702b": "b635c45a19afd1b25af3c2fbc5101d93",
".git/objects/43/508c0adbf776b3fe429c644ff42d369e5af644": "f903c0af7ea28455c9d2a30d198bb37e",
".git/objects/2c/866b484a042f11e73f41c9ad3942d47380d8f9": "8b481f0a744766e6e12dcb4b3f192907",
".git/objects/f2/04823a42f2d890f945f70d88b8e2d921c6ae26": "6b47f314ffc35cf6a1ced3208ecc857d",
".git/objects/e3/e9ee754c75ae07cc3d19f9b8c1e656cc4946a1": "14066365125dcce5aec8eb1454f0d127",
".git/objects/02/1d4f3579879a4ac147edbbd8ac2d91e2bc7323": "17f03e66ae7084386b483f2a6fea5e57",
".git/objects/b9/2a0d854da9a8f73216c4a0ef07a0f0a44e4373": "f62d1eb7f51165e2a6d2ef1921f976f3",
".git/objects/10/565a2751eedcd16078ee1f3862b85d7c2ce5f4": "ebe68e255ddfab9e42c3bf94d48debd9",
".git/objects/35/5b08993e3a24d761c86de97db2fc1c52021928": "caa238e57e20511907d95a1aaa518291",
".git/objects/35/6c9c516c8124c7bd67ca6acbceaca789e5ada3": "ba3ba8e7d587de0dba88ce20accd9b78",
".git/objects/f1/f9b680b545f5d3cefd454918907718d1b57310": "734754f72e2183367d90866d156eebec",
".git/objects/2f/10f4f56d991c9ebd91d473bb35f1665459d13c": "7db38d85e5189a3909b5f1b7d84f5df7",
".git/objects/dc/82976e2d953df2826b7d3cd1540f5a3c7cc9f1": "4cce59bc592ec0618fe89dc5526cdc3e",
".git/objects/bf/1b55117b3ba46b9b2fce313f4bbf323761b21b": "8f9bb3f941e3c5c3bc2ccf65fa209be3",
".git/objects/d3/b092d34651bb13354832d33b2bd97351aae8e0": "4503f99470a47a097e03ca0fad618760",
".git/objects/14/fc65065275aeb8990cd4c7bc32eacc0a8d081a": "5358af7eb1abdf3b7708364c7752bf05",
".git/objects/05/4cb8839b5ff62b2af223d22d92990269c6ff48": "af25d97f959333733cc13d742a4a06bb",
".git/objects/05/bd27d0afca13b5b698f06397083977b2fcaa9c": "e700ded5c911795cc13c0d88782ea5e9",
".git/objects/ef/39d31138c93e220f820dec89a3e3f91f2b0a73": "d1d275beb9ebae8d025668095d585a89",
".git/objects/e5/1ca077da995ba5ddc6b652f3ae638a3c00685d": "588efb688990818bef822f5d55db6b68",
".git/objects/e5/1248d8b716316830efcc3a80ff251a5cb2a243": "34c4137827d291ede25f4010e84779b3",
".git/objects/e8/593bcef39af745d7f7e6f0f339c3521a600899": "2ec4385fea61d3b5089bbb3371f0fc9d",
".git/objects/e8/dfc3ffdc64d921749bc4ec104fc11b40b41aae": "b58bc111aa7def51f11c2440b01b4fd1",
".git/objects/a9/9d510c20040ecdb1cd54027f224e1bf5e96651": "63adc4d0696aebae51c040c399115d18",
".git/objects/1a/bbe3d4abadb2179668ea36d7d32a2b77640721": "5d3a5eeb24d6272c37e7e212e3f4fae7",
".git/objects/df/191376a17ca3861081e400659f8efc72a85bdd": "87241833b97b8bc11c8d4413daace099",
".git/objects/5d/d4a9ef730439688d1fa0911b972aaa3b22f6ad": "2f415e11773698121ddf19299b4318b9",
".git/objects/fd/00c020a1a04b5703cb529d6ffdb031d3fe69b8": "5a4aeb45c531420c754fffe7b5f4432f",
".git/objects/00/b24661ef943cf8980393d501edab50ee18344c": "27c5a188a14a048d63370c8a1e661596",
".git/objects/78/3652e1f9a6a35d61d78592540787402f868cfb": "7ee8b1cd65011b6af8d8b7d3d1b014f7",
".git/objects/78/3400a5ee2a84b6fbf88caaf64f8fa7072c94f3": "1e9337a7a6361963cfa1797752583617",
".git/objects/e2/df487a24acc598100996b3f86f3fc05e9155fe": "eff7e19fc4ba32ed90ac5a25a1e6ac14",
".git/objects/97/cea9649c406a7c71c6b97bd2c1ba10589088b6": "fe625d051ca5535ac314e3e60ea8f034",
".git/objects/aa/cdb95712a53fda4b57c3c061e2815ca5f616b9": "3f40b662090a87c9471ca325de06d151",
".git/objects/a7/0714a3c49fdab3eda9ad218e92fd22f820a989": "75b3472eae87196c8657062eb4ae0d5a",
".git/objects/37/ebe4236efdaacefb93273b389b506825b0482b": "bbf975059d507df1b0c292ecb63b7691",
".git/objects/82/039646eae58381941a128edf3dd254c98a2961": "ed5ffad7ec763c7e7391b0219d94b0ca",
".git/objects/ea/986bbe9983d634ac342f9600d48ac030cf2198": "3feb4c09530b014d15ad3a60ebe5910a",
".git/objects/81/b9565365b0fe6977f11e4cfdc5c68a47c90cb0": "2b8cceb45b080393f5403ca1c80eaa25",
".git/objects/b2/7fdd90ab3d1cd8b13667394351b48fbe7f57ef": "cca43b203a4af097711ebfddc3f2308b",
".git/objects/19/6d0937683a69215348d5f5c55913c5a20215f1": "05b0f8a5a01c579dd08aa2d0a5311655",
".git/objects/48/680e783eede4bb701ede58f2a303ad436cb3cb": "1423cf3dd6cd7180f14204bbb20974d1",
".git/objects/fa/2d2b4ec825d1626560d22aadca12283e095430": "e8e53746d9cd7c5b6935aa63a5dabfa2",
".git/objects/83/8c87dd07c155f4ad6f02d3a1021b887bbcd997": "09169aff668d598515881c7f013175b8",
".git/COMMIT_EDITMSG": "634861187cd95de4c014e030b56ce12c",
".git/logs/HEAD": "6491905bfb6fd42b31df9d622f6611e4",
".git/logs/refs/heads/main": "e7276ce033a6d97fd6b6bf3168177ad7",
".git/logs/refs/remotes/origin/main": "a6052d585bc3770b8ee18fb1d4fd382d",
".git/logs/refs/remotes/origin/HEAD": "b5a3ccdd8d2dce8bd59a9831bf21f82a",
".git/HEAD": "cf7dd3ce51958c5f13fece957cc417fb",
".git/config": "2a808f48e10ce8660f7044bbe87a2be3",
".git/FETCH_HEAD": "de58f1022b12f604c2387e7a68d77969",
".git/ORIG_HEAD": "efd960e0ae6d403f3f9e1d968b170999",
".git/index": "0eaf81ea450915c8a658fc9306f2c70c",
"CNAME": "c2d7c369bc6f93bc2255a70b40351311"};
// The application shell files that are downloaded before a service worker can
// start.
const CORE = ["main.dart.js",
"index.html",
"flutter_bootstrap.js",
"assets/AssetManifest.bin.json",
"assets/FontManifest.json"];

// During install, the TEMP cache is populated with the application shell files.
self.addEventListener("install", (event) => {
  self.skipWaiting();
  return event.waitUntil(
    caches.open(TEMP).then((cache) => {
      return cache.addAll(
        CORE.map((value) => new Request(value, {'cache': 'reload'})));
    })
  );
});
// During activate, the cache is populated with the temp files downloaded in
// install. If this service worker is upgrading from one with a saved
// MANIFEST, then use this to retain unchanged resource files.
self.addEventListener("activate", function(event) {
  return event.waitUntil(async function() {
    try {
      var contentCache = await caches.open(CACHE_NAME);
      var tempCache = await caches.open(TEMP);
      var manifestCache = await caches.open(MANIFEST);
      var manifest = await manifestCache.match('manifest');
      // When there is no prior manifest, clear the entire cache.
      if (!manifest) {
        await caches.delete(CACHE_NAME);
        contentCache = await caches.open(CACHE_NAME);
        for (var request of await tempCache.keys()) {
          var response = await tempCache.match(request);
          await contentCache.put(request, response);
        }
        await caches.delete(TEMP);
        // Save the manifest to make future upgrades efficient.
        await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
        // Claim client to enable caching on first launch
        self.clients.claim();
        return;
      }
      var oldManifest = await manifest.json();
      var origin = self.location.origin;
      for (var request of await contentCache.keys()) {
        var key = request.url.substring(origin.length + 1);
        if (key == "") {
          key = "/";
        }
        // If a resource from the old manifest is not in the new cache, or if
        // the MD5 sum has changed, delete it. Otherwise the resource is left
        // in the cache and can be reused by the new service worker.
        if (!RESOURCES[key] || RESOURCES[key] != oldManifest[key]) {
          await contentCache.delete(request);
        }
      }
      // Populate the cache with the app shell TEMP files, potentially overwriting
      // cache files preserved above.
      for (var request of await tempCache.keys()) {
        var response = await tempCache.match(request);
        await contentCache.put(request, response);
      }
      await caches.delete(TEMP);
      // Save the manifest to make future upgrades efficient.
      await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
      // Claim client to enable caching on first launch
      self.clients.claim();
      return;
    } catch (err) {
      // On an unhandled exception the state of the cache cannot be guaranteed.
      console.error('Failed to upgrade service worker: ' + err);
      await caches.delete(CACHE_NAME);
      await caches.delete(TEMP);
      await caches.delete(MANIFEST);
    }
  }());
});
// The fetch handler redirects requests for RESOURCE files to the service
// worker cache.
self.addEventListener("fetch", (event) => {
  if (event.request.method !== 'GET') {
    return;
  }
  var origin = self.location.origin;
  var key = event.request.url.substring(origin.length + 1);
  // Redirect URLs to the index.html
  if (key.indexOf('?v=') != -1) {
    key = key.split('?v=')[0];
  }
  if (event.request.url == origin || event.request.url.startsWith(origin + '/#') || key == '') {
    key = '/';
  }
  // If the URL is not the RESOURCE list then return to signal that the
  // browser should take over.
  if (!RESOURCES[key]) {
    return;
  }
  // If the URL is the index.html, perform an online-first request.
  if (key == '/') {
    return onlineFirst(event);
  }
  event.respondWith(caches.open(CACHE_NAME)
    .then((cache) =>  {
      return cache.match(event.request).then((response) => {
        // Either respond with the cached resource, or perform a fetch and
        // lazily populate the cache only if the resource was successfully fetched.
        return response || fetch(event.request).then((response) => {
          if (response && Boolean(response.ok)) {
            cache.put(event.request, response.clone());
          }
          return response;
        });
      })
    })
  );
});
self.addEventListener('message', (event) => {
  // SkipWaiting can be used to immediately activate a waiting service worker.
  // This will also require a page refresh triggered by the main worker.
  if (event.data === 'skipWaiting') {
    self.skipWaiting();
    return;
  }
  if (event.data === 'downloadOffline') {
    downloadOffline();
    return;
  }
});
// Download offline will check the RESOURCES for all files not in the cache
// and populate them.
async function downloadOffline() {
  var resources = [];
  var contentCache = await caches.open(CACHE_NAME);
  var currentContent = {};
  for (var request of await contentCache.keys()) {
    var key = request.url.substring(origin.length + 1);
    if (key == "") {
      key = "/";
    }
    currentContent[key] = true;
  }
  for (var resourceKey of Object.keys(RESOURCES)) {
    if (!currentContent[resourceKey]) {
      resources.push(resourceKey);
    }
  }
  return contentCache.addAll(resources);
}
// Attempt to download the resource online before falling back to
// the offline cache.
function onlineFirst(event) {
  return event.respondWith(
    fetch(event.request).then((response) => {
      return caches.open(CACHE_NAME).then((cache) => {
        cache.put(event.request, response.clone());
        return response;
      });
    }).catch((error) => {
      return caches.open(CACHE_NAME).then((cache) => {
        return cache.match(event.request).then((response) => {
          if (response != null) {
            return response;
          }
          throw error;
        });
      });
    })
  );
}
