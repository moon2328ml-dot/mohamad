import "jsr:@supabase/functions-js/edge-runtime.d.ts";

const cors = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

const instructions = `تو «همراه محمد» هستی؛ یک برادر بزرگ‌تر مهربان، صریح و واقع‌گرا برای رشد فردی، برنامه‌ریزی، کتاب‌خوانی، پول، رابطه و خودشناسی. فارسی روان و محاوره‌ای جواب بده و محمد را «داداش» صدا کن. اول حرفش را بفهم، بعد یک پاسخ مشخص و کاربردی بده. اگر سؤال مبهم است فقط یک سؤال روشن‌کننده بپرس. پاسخ‌ها کوتاه اما عمیق باشند و در پایان حداکثر یک قدم کوچک برای امروز پیشنهاد کن. ادعای روان‌درمانگر یا پزشک بودن نکن. اگر نشانهٔ خطر فوری، خودآسیب‌زنی یا آسیب به دیگری دیدی، با همدلی توصیه کن تنها نماند و فوراً با فرد قابل اعتماد یا اورژانس محل تماس بگیرد.`;

function json(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { ...cors, "Content-Type": "application/json" },
  });
}

function extractText(data: any): string {
  if (typeof data?.output_text === "string") return data.output_text.trim();
  const chunks: string[] = [];
  for (const item of data?.output || []) {
    for (const part of item?.content || []) {
      if (typeof part?.text === "string") chunks.push(part.text);
    }
  }
  return chunks.join("\n").trim();
}

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: cors });
  if (req.method !== "POST") return json({ error: "POST only" }, 405);
  try {
    const body = await req.json();
    const message = String(body?.message || "").trim();
    if (!message || message.length > 12000) return json({ error: "پیام خالی یا خیلی طولانی است" }, 400);
    const apiKey = Deno.env.get("OPENAI_API_KEY") || String(body?.apiKey || "").trim();
    if (!apiKey) return json({ error: "کلید مدل روی سرور تنظیم نشده است" }, 503);
    const model = Deno.env.get("OPENAI_MODEL") || "gpt-6-astra";
    const context = JSON.stringify(body?.context || {}).slice(0, 18000);
    const upstream = await fetch("https://api.openai.com/v1/responses", {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${apiKey}` },
      body: JSON.stringify({
        model,
        instructions,
        input: `پیام محمد:\n${message}\n\nخلاصهٔ مجاز از اطلاعات شخصی NEVo برای شخصی‌سازی:\n${context}`,
        store: false,
      }),
    });
    const data = await upstream.json();
    if (!upstream.ok) return json({ error: data?.error?.message || "سرویس مدل پاسخ نداد" }, upstream.status >= 500 ? 502 : upstream.status);
    const reply = extractText(data);
    if (!reply) return json({ error: "مدل پاسخ متنی نداد" }, 502);
    return json({ reply, model });
  } catch (error) {
    return json({ error: error instanceof Error ? error.message : "خطای داخلی سرور" }, 500);
  }
});
