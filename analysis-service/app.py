from flask import Flask, request, jsonify, send_file
import pandas as pd
import matplotlib.pyplot as plt
import io
import json
import os

app = Flask(__name__)

# Carrega o dicionário de setores a partir do arquivo JSON
def load_sector_map():
    config_path = os.path.join(os.path.dirname(__file__), 'sectors.json')
    try:
        with open(config_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    except FileNotFoundError:
        print(f"Aviso: Arquivo {config_path} não encontrado. Usando dicionário vazio.")
        return {}
    except json.JSONDecodeError:
        print(f"Aviso: Erro ao decodificar {config_path}. O arquivo contém JSON inválido.")
        return {}

TICKER_SECTOR_MAP = load_sector_map()

@app.route('/analyze/diversification', methods=['POST'])
def analyze_diversification():
    portfolio_data = request.json
    assets = portfolio_data.get('assets', [])

    if not assets:
        return jsonify({"error": "No assets provided"}), 400

    # 1. Usa Pandas para processar os dados
    df = pd.DataFrame(assets)
    # Se o ticker não estiver no TICKER_SECTOR_MAP, será classificado como "Outros"
    df['sector'] = df['ticker'].map(TICKER_SECTOR_MAP).fillna("Outros")

    # Simula o valor total de cada ativo (quantidade * preço)
    # A API da Brapi não fornece o valor total, então vamos usar o preço médio para o cálculo
    df['totalValue'] = df['quantity'] * df['averagePrice']

    diversification = df.groupby('sector')['totalValue'].sum()

    # Prevenção contra erro do Matplotlib caso o valor total seja 0
    if diversification.sum() == 0:
        return jsonify({"error": "Total portfolio value is zero"}), 400

    # 2. Usa Matplotlib para gerar o gráfico de pizza
    plt.style.use('seaborn-v0_8-darkgrid')
    fig, ax = plt.subplots(figsize=(8, 6))
    ax.pie(diversification, labels=diversification.index, autopct='%1.1f%%', startangle=90)
    ax.axis('equal') # Garante que a pizza seja um círculo.
    ax.set_title('Diversificação da Carteira por Setor')

    # 3. Salva a imagem em um buffer de memória em vez de um arquivo
    buf = io.BytesIO()
    plt.savefig(buf, format='png', bbox_inches='tight')
    buf.seek(0)
    plt.close(fig)

    # 4. Retorna a imagem como uma resposta HTTP
    return send_file(buf, mimetype='image/png')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)
